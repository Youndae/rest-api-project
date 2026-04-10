package com.example.boardrest.auth.filter;

import com.example.boardrest.auth.oAuth.CustomOAuth2User;
import com.example.boardrest.auth.user.CustomUserDetails;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.auth.oAuth.domain.OAuth2DTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enums.TokenValidationResult;
import com.example.boardrest.properties.CookieProperties;
import com.example.boardrest.properties.TokenProperties;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.auth.user.CustomUser;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;


@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenProperties tokenProperties;

    private final CookieProperties cookieProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        Cookie accessTokenCookie = WebUtils.getCookie(request, tokenProperties.getAccess().getHeader());
        Cookie refreshTokenCookie = WebUtils.getCookie(request, tokenProperties.getRefresh().getHeader());
        Cookie inoCookie = WebUtils.getCookie(request, cookieProperties.getIno().getHeader());
        //인증객체 생성시 필요한 사용자 아이디
        String username = null;

        if (inoCookie != null) {
            if (accessTokenCookie != null && refreshTokenCookie != null) { //모든 토큰이 존재
                if(!accessTokenCookie.getValue().startsWith(tokenProperties.getPrefix())
                        || !refreshTokenCookie.getValue().startsWith(tokenProperties.getPrefix())){
                    chain.doFilter(request, response);
                    return;
                }else {
                    String inoValue = inoCookie.getValue();
                    String claimByUserIdToAccessToken = jwtTokenProvider.verifyAccessToken(accessTokenCookie, inoValue);

                    //토큰 검증 과정에서 탈취 또는 잘못된 토큰이라는 응답이 반환되는 경우 클라이언트 쿠키가 삭제되도록 response에 담아 반환
                    if (claimByUserIdToAccessToken.equals(TokenValidationResult.TOKEN_STEALING.getResult())
                            || claimByUserIdToAccessToken.equals(TokenValidationResult.WRONG_TOKEN.getResult())) {
                        deleteTokenCookieThrowException(response);
                        return;
                    } else if (claimByUserIdToAccessToken.equals(TokenValidationResult.TOKEN_EXPIRATION.getResult())) { //AccessToken 만료 응답
                        claimByUserIdToAccessToken = jwtTokenProvider.decodeToken(accessTokenCookie);//accessToken 복호화
                        String verifyRefreshTokenResult = jwtTokenProvider.verifyRefreshToken(
                                                                refreshTokenCookie, inoValue, claimByUserIdToAccessToken
                                                            );//refreshToken 검증
                        // 복호화한 AccessToken과 refreshToken Claim이 일치한다면 재발급 수행
                        if (verifyRefreshTokenResult.equals(claimByUserIdToAccessToken)) {
                            jwtTokenProvider.issuedToken(claimByUserIdToAccessToken, inoValue, response);
                            username = claimByUserIdToAccessToken;//이후 인증객체 처리를 위해 사용자 아이디를 username 변수에 담아준다.
                        } else if (verifyRefreshTokenResult.equals(TokenValidationResult.TOKEN_STEALING.getResult())
                                || verifyRefreshTokenResult.equals(TokenValidationResult.WRONG_TOKEN.getResult())) {
                            //일치하지 않는 경우 결과가 탈취로 반환. 탈취 또는 잘못된 토큰 응답이 반환되면 redis 데이터와 쿠키 삭제
                            deleteTokenAndCookieThrowException(claimByUserIdToAccessToken, inoValue, response);
                            return;
                        }
                    } else {
                        //AccessToken 검증 정상 응답
                        username = claimByUserIdToAccessToken;
                    }
                }
            } else if (accessTokenCookie == null && refreshTokenCookie == null) {
                chain.doFilter(request, response);
                return;
            } else {
                //토큰 두개중 하나만 존재하기 때문에 탈취로 판단.
                //두개 중 존재하는 토큰을 복호화하고 그 Claim 값을 통해 redis 데이터 삭제 및 쿠키 삭제
                String claimByUserId;
                if (accessTokenCookie != null)
                    claimByUserId = jwtTokenProvider.decodeToken(accessTokenCookie);
                else
                    claimByUserId = jwtTokenProvider.decodeToken(refreshTokenCookie);

                deleteTokenAndCookieThrowException(claimByUserId, inoCookie.getValue(), response);
                return;
            }
        }

        if (username != null) {
            Member memberEntity = memberRepository.findByUserId(username);
            String userId;
            Collection<? extends GrantedAuthority> authorities;

            CustomUserDetails userDetails;

            if(memberEntity.getProvider().equals("local")){
                userDetails = new CustomUser(memberEntity);
            }else{
                OAuth2DTO oAuth2DTO = memberEntity.toOAuth2DTOUseFilter();
                userDetails = new CustomOAuth2User(oAuth2DTO);
            }

            userId = userDetails.getUserId();
            authorities = userDetails.getAuthorities();

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    public void tokenStealingExceptionResponse(HttpServletResponse response) {
        response.setStatus(ErrorCode.TOKEN_STEALING.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }

    public void deleteTokenAndCookieThrowException(String tokenClaim, String inoValue, HttpServletResponse response) {
        jwtTokenProvider.deleteToken(tokenClaim, inoValue, response);
        tokenStealingExceptionResponse(response);
    }

    public void deleteTokenCookieThrowException(HttpServletResponse response) {
        jwtTokenProvider.deleteTokenCookie(response);
        tokenStealingExceptionResponse(response);
    }

}
