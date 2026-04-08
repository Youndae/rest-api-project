package com.example.boardrest.auth.handler;

import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.response.ExceptionResponse;
import com.example.boardrest.domain.enumuration.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper om;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getHttpStatus().value())
                .message(ResponseStatus.FAIL.getMessage())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(om.writeValueAsString(exceptionResponse));
    }
}
