package com.example.boardrest.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      log.info("Success Handler");

        List<String> roleNames = new ArrayList<>();

        authentication.getAuthorities().forEach(authority -> {
            roleNames.add(authority.getAuthority());
        });

//        response.sendRedirect("/board/boardList");

//        response.addHeader("JSESSIONID", String.valueOf(SecurityContextHolder.getContext().getAuthentication()));

        log.info("session : " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        log.info("cookie : " + response.getHeaderNames());

        log.info("cookie jsessionid : " + response.getHeader("JSESSIONID"));
    }
}
