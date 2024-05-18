package com.example.boardrest.config.oAuth;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String userId = customOAuth2User.getUserId();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        Cookie inoCookie = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        if(inoCookie == null)
            tokenProvider.issuedAllToken(userId, response);
        else
            tokenProvider.issuedToken(userId, inoCookie.getValue(), response);

        if(customOAuth2User.getNickname() == null){
            response.sendRedirect("http://localhost:3000/member/profile");
        }else{
            response.sendRedirect("http://localhost:3000/member/oAuth");
        }


    }
}
