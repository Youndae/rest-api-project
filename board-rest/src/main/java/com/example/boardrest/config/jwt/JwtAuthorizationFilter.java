package com.example.boardrest.config.jwt;

import com.example.boardrest.customException.CustomTokenStealingException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.security.domain.CustomUser;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider){
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain)
            throws IOException, ServletException {
        log.info("Authorities request");

        Cookie accessTokenCookie = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie refreshTokenCookie = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);
        String inoValue = ino == null ? null : ino.getValue();
        String username = null;

        //토큰 두개가 전부 존재하지 않는다면 검증하지 않고 넘긴다. 비로그인으로 판단할 수 있기 때문.
        //ino의 경우 ino만 존재해도 검증하지 않고 넘긴다는 점은 동일하기 때문에 굳이 판단할 필요가 없다.
        if(accessTokenCookie == null && refreshTokenCookie == null){
            chain.doFilter(request, response);
            return;
        }

        //ino가 없는데 토큰이 존재할 수 없기 때문에 탈취로 판단한다.
        //하지만 ino가 없기 때문에 redis 데이터를 조회할 수 없어 쿠키만 삭제하도록 조치한다.
        if(ino == null)
            deleteTokenCookieThrowException(response);

        //accessToken이 존재하는데 refreshToken이 존재하지 않을 수 없기 때문에 탈취로 판단.
        if(accessTokenCookie != null && refreshTokenCookie == null){
            String accessTokenClaim = jwtTokenProvider.verifyAccessToken(accessTokenCookie, inoValue);

            //반환받은 claim이 null이 아니고 "st"도 아니라면 정상적으로 아이디가 반환된것이기 때문에 Redis 데이터와 쿠키 삭제.
            if(accessTokenClaim != null && !accessTokenClaim.equals("st"))
                deleteTokenAndCookieThrowException(accessTokenClaim, inoValue, response);
            else //반환받은 claim이 null이거나 "st"라면 응답 쿠키만 제거
                deleteTokenCookieThrowException(response);
        }

        //ino와 refreshToken은 null이 아니지만 accessToken은 null이라면
        //재발급을 할지 탈취로 판단할지 체크한다.
        //재발급이라면 username에 사용자 아이디를 담는다.
        if(accessTokenCookie == null){
            String refreshTokenClaim = jwtTokenProvider.verifyRefreshToken(refreshTokenCookie, inoValue);

            //claim이 null이라면 잘못된 토큰이므로 데이터에 접근할 수 없어 응답 쿠키만 제어하고
            //"st"라면 탈취로 판단, Redis 데이터를 제거한 뒤에 반환하기 때문에 쿠키만 제어한다.
            if(refreshTokenClaim == null && refreshTokenClaim.equals("st")){
                deleteTokenCookieThrowException(response);
            }else {
                //claim이 정상적으로 반환되었기 때문에 username에 사용자 아이디를 담아주고 accessToken, refreshToken을 재발급한다.
                username = refreshTokenClaim;
                jwtTokenProvider.issuedToken(username, inoValue, response);
            }
        }else {
            //모든 토큰이 있기 때문에 검증 처리 후 탈취 판단.
            //정상이라면 username에 담을 사용자 아이디를 반환받아야 한다.
            String accessTokenClaim = jwtTokenProvider.verifyAccessToken(accessTokenCookie, inoValue);
            String refreshTokenClaim = jwtTokenProvider.verifyRefreshToken(refreshTokenCookie, inoValue);

            /**
             * 1. 둘중 하나라도 st를 반환받았다면 탈취된 토큰으로 Redis 데이터를 제거한 뒤 반환받으므로 응답쿠키만 제어.
             * 2. 둘다 null을 반환받았다면 잘못된 토큰이므로 응답 쿠키만 제어
             * 3. 둘중 하나라도 null인 경우 정상적인 토큰에서 반환받은 claim으로 redis 데이터 제거와 응답쿠키 제어.
             * 4. 위 조건에 모두 만족하지 않는다면 둘다 정상적인 토큰이므로 username에 반환받은 claim을 담아준다.
             */
            if(accessTokenClaim.equals("st") || refreshTokenClaim.equals("st"))
                deleteTokenCookieThrowException(response);
            else if(accessTokenClaim == null && refreshTokenClaim == null)
                deleteTokenCookieThrowException(response);
            else if(accessTokenClaim == null || refreshTokenClaim == null) {
                String deleteUid = accessTokenClaim != null ? accessTokenClaim : refreshTokenClaim;
                deleteTokenAndCookieThrowException(deleteUid, inoValue, response);
            }else
                username = accessTokenClaim;
        }

        if(username != null){
            Member memberEntity = memberRepository.findByUserId(username);
            CustomUser customUser = new CustomUser(memberEntity);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }

    public void deleteTokenAndCookieThrowException(String tokenClaim, String inoValue, HttpServletResponse response) {
        jwtTokenProvider.deleteToken(tokenClaim, inoValue, response);
        throw new CustomTokenStealingException(ErrorCode.TOKEN_STEALING, "CustomTokenStealingException");
    }

    public void deleteTokenCookieThrowException(HttpServletResponse response){
        jwtTokenProvider.deleteTokenCookie(response);
        throw new CustomTokenStealingException(ErrorCode.TOKEN_STEALING, "CustomTokenStealingException");
    }

}
