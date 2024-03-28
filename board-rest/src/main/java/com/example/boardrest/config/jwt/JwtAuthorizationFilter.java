package com.example.boardrest.config.jwt;

import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain)
            throws IOException, ServletException {

        Cookie accessTokenCookie = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie refreshTokenCookie = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);
        //인증객체 생성시 필요한 사용자 아이디
        String username = null;

        /**
         * 1. ino == null 이라면
         *      ino가 존재하지 않고 accessToken, RefreshToken 둘중 하나 또는 둘다 존재하는 경우는 무조건 탈취로 판단할 수 있다.
         *      또한, 3개의 쿠키 모두 null인 경우도 이 조건문에 걸릴 수 있다.
         *      위와 같은 탈취는 탈취된 데이터를 ino 없이 처리할 수 없고 인증이 필요한 요청 역시 수행할 수 없기 때문에 조치를 취하지 않고 넘긴다.
         *
         * 2. ino != null && (accessTokenCookie == null && refreshTokenCookie == null) 이라면
         *      accessTokenCookie와 refreshTokenCookie는 30일의 만료시간, ino는 9999의 사실상 무기한의 만료시간을 갖는다.
         *      그렇기 때문에 장기간 요청이 발생하지 않는 경우 ino만 존재할 수 있고 재로그인으로 토큰을 발급받을 수 있어야 하므로 검증하지 않고 진행하도록 처리한다.
         *
         * 3. ino != null && (accessTokenCookie != null && refreshTokenCookie != null) 이라면
         *      모든 쿠키가 존재하기 때문에 검증하고 검증 결과에 따라 진행 또는 토큰 탈취 응답을 반환한다.
         *
         * 4. 위 모든 조건에 해당하지 않는다면
         *      ino가 존재하는데 토큰이 둘중 하나만 존재할수는 없기 때문에 무조건 탈취로 판단해 탈취 응답을 반환한다.
         *      첫 조건문과 다르게 ino와 하나의 토큰이 존재한다면 탈취된 데이터를 찾아내 제거할 수 있으므로 제거 처리 후 탈취 응답을 반환한다.
         *
         *
         * if(ino != null)
         *      if(accessTokenCookie != null && refreshTokenCookie != null)
         *          check Token
         *      else if(accessTokenCookie == null && refreshTokenCookie == null)
         *          chain.doFilter();
         *      else  (ino != null, at != null || rt != null)
         *          TokenStealing
         * else (ino == null)
         *      ino가 없다면 탈취로 판단되더라도 할 수 있는게 없으므로 chain.doFilter
         *
         *
         * 테스트
         * 1. 모든 쿠키가 존재하지 않을 때 정상적으로 동작하는가
         * 2. ino만 존재할 때 정상적으로 동작하는가
         * 3. 모든 쿠키가 존재할 때 정상적으로 동작하는가
         * 4. at, rt 둘중 하나가 없다면 정상적으로 탈취라고 판단하고 응답하는가
         * 5. 저장된 토큰이 아닌 다른 토큰을 전달했을 때 정상적으로 탈취라고 판단하는가
         * 6. ino가 없는데 토큰이 존재할 때 정상적으로 처리하는가
         * 7. 탈취 프로세스 처리 후 customTokenStealingException을 통해 제대로 응답 전달이 되는가. 클라이언트에서는 정상적으로 처리하는가.
         *
         *
         */

        //최종
        if (inoCookie != null) {
            if (accessTokenCookie != null && refreshTokenCookie != null) {
                //모든 토큰이 존재
                String inoValue = inoCookie.getValue();
                String claimByUserIdToAccessToken = jwtTokenProvider.verifyAccessToken(accessTokenCookie, inoValue);

                if (claimByUserIdToAccessToken.equals(JwtProperties.TOKEN_STEALING_RESULT) || claimByUserIdToAccessToken.equals(JwtProperties.WRONG_TOKEN)) {
                    //토큰 검증 과정에서 탈취로 판단되었다면 redis 데이터는 삭제된 후 TOKEN_STEALING_RESULT가 반환된다.
                    //쿠키 삭제를 위한 응답 쿠키만 처리한다.
                    //WRONG_TOKEN 응답의 경우는 토큰이 잘못된 토큰이라 JWTDecodeException이 발생한 경우이므로
                    //따로 처리할 것은 없이 응답으로 쿠키를 삭제하도록 처리한다.
                    deleteTokenCookieThrowException(response);
                    return;
                } else if (claimByUserIdToAccessToken.equals(JwtProperties.TOKEN_EXPIRATION_RESULT)) {
                    //accessToken 복호화
                    claimByUserIdToAccessToken = jwtTokenProvider.decodeToken(accessTokenCookie);
                    //복호화 결과인 사용자 아이디로 refreshToken 검증 및 redis 데이터와 비교 후 일치한다면 아이디가 반환됨.
                    String verifyRefreshTokenResult = jwtTokenProvider.verifyRefreshToken(refreshTokenCookie, inoValue, claimByUserIdToAccessToken);
                    //일치하는 경우 토큰 재발급 수행.
                    if (verifyRefreshTokenResult.equals(claimByUserIdToAccessToken)) {
                        //토큰이 모두 정상이기 때문에 accessToken과 refreshToken을 재발급한다.
                        jwtTokenProvider.issuedToken(claimByUserIdToAccessToken, inoValue, response);
                        //이후 인증객체 처리를 위해 사용자 아이디를 username 변수에 담아준다.
                        username = claimByUserIdToAccessToken;
                    } else if (verifyRefreshTokenResult.equals(JwtProperties.TOKEN_STEALING_RESULT) || verifyRefreshTokenResult.equals(JwtProperties.WRONG_TOKEN)) {
                        //일치하지 않는다면 "st" 반환으로 탈취 프로세스
                        //refreshToken claim 데이터는 삭제된 뒤 결과가 반환되기 때문에
                        //accessToken claim 데이터에 대한 삭제 처리 및 응답 쿠키 생성을 처리한다.
                        //WRONG_TOKEN이 응답되면 잘못된 토큰값이므로 탈취와 같은 처리를 수행한다.
                        deleteTokenAndCookieThrowException(claimByUserIdToAccessToken, inoValue, request, response);
                        return;
                    }
                } else {
                    //토큰이 정상이라면
                    username = claimByUserIdToAccessToken;
                }
            } else if (accessTokenCookie == null && refreshTokenCookie == null) {
                chain.doFilter(request, response);
                return;
            } else {
                //토큰 두개중 하나만 존재하기 때문에 탈취로 판단.
                String claimByUserId;
                if (accessTokenCookie != null)
                    claimByUserId = jwtTokenProvider.decodeToken(accessTokenCookie);
                else
                    claimByUserId = jwtTokenProvider.decodeToken(refreshTokenCookie);

                deleteTokenAndCookieThrowException(claimByUserId, inoCookie.getValue(), request, response);
                return;
            }
        }

        if (username != null) {
            Member memberEntity = memberRepository.findByUserId(username);
            CustomUser customUser = new CustomUser(memberEntity);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    public void tokenStealingExceptionResponse(HttpServletResponse response) {
        response.setStatus(ErrorCode.TOKEN_STEALING.getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }

    public void deleteTokenAndCookieThrowException(String tokenClaim, String inoValue, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("deleteTokenAnd Cookie Exception");
        jwtTokenProvider.deleteToken(tokenClaim, inoValue, response);
        tokenStealingExceptionResponse(response);
    }

    public void deleteTokenCookieThrowException(HttpServletResponse response) {
        System.out.println("delete Cookie Excpetion");
        jwtTokenProvider.deleteTokenCookie(response);
        tokenStealingExceptionResponse(response);
    }

}
