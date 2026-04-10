package com.example.boardrest.auth.handler;

import com.example.boardrest.domain.dto.member.out.MemberStatusResponse;
import com.example.boardrest.domain.dto.response.ApiResponse;
import com.example.boardrest.domain.enums.ResponseStatus;
import com.example.boardrest.properties.CookieProperties;
import com.example.boardrest.service.AuthContextService;
import com.example.boardrest.service.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper om;

    private final JwtTokenProvider tokenProvider;

    private final AuthContextService authContextService;

    private final CookieProperties cookieProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        MemberStatusResponse responseContent = authContextService.getMemberStatus(authentication);
        Cookie inoCookie = WebUtils.getCookie(request, cookieProperties.getIno().getHeader());
        String userId = responseContent.getUserId();

        if(inoCookie == null)
            tokenProvider.issuedAllToken(userId, response);
        else
            tokenProvider.issuedToken(userId, inoCookie.getValue(), response);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(ResponseStatus.SUCCESS.getMessage())
                .content(responseContent)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(om.writeValueAsString(apiResponse));
    }
}
