package com.example.boardrest.auth.filter;

import com.example.boardrest.domain.dto.member.in.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper om;

    public LoginFilter(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if(!request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not post: " + request.getMethod());

        try {
            LoginRequest loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword());

            return this.getAuthenticationManager().authenticate(authenticationToken);
        }catch(IOException e) {
            throw new AuthenticationServiceException("Failed to parse loginRequest body: ", e);
        }
    }
}
