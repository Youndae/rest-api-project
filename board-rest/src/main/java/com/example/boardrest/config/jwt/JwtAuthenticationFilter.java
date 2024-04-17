package com.example.boardrest.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.security.domain.CustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Deprecated
 */
@Deprecated
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("#{jwt['token.access.secret']}")
    private final String accessSecret;

    private final AuthenticationManager authenticationManager;

//    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request
                            , HttpServletResponse response) throws AuthenticationException {

        log.info("JwtAuthenticationFilter's attemptAuthentication");

        try{
            ObjectMapper om = new ObjectMapper();
            Member member = om.readValue(request.getInputStream(), Member.class);

            log.info("attemptAuthentication member : " + member);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUserId(), member.getUserPw());

            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            log.info("login Success : " + customUser.getUsername());

            return authentication;
        }catch (IOException e){
            log.info("attemptAuthentication Exception");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request
                            , HttpServletResponse response
                            , FilterChain chain
                            , Authentication authResult) throws IOException, ServletException {

        log.info("JwtAuthenticationFilter's successfulAuthentication");

        CustomUser customUser = (CustomUser) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("cocoToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000))
                .withClaim("userId", customUser.getMember().getUserId())
                .sign(Algorithm.HMAC512(accessSecret));

        log.info("AuthenticationFilter's token : " + jwtToken);

        Cookie cookie = new Cookie(JwtProperties.ACCESS_HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        cookie.setPath("/");
//        cookie.setMaxAge(JwtProperties.MAX_AGE);
        cookie.isHttpOnly();
        response.addCookie(cookie);

        response.addHeader(JwtProperties.ACCESS_HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
