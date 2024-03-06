package com.example.boardrest.security;

import com.example.boardrest.config.jwt.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Deprecated
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request
                                    , HttpServletResponse response
                                    , Authentication authentication)
                                    throws IOException, ServletException {

        log.info("LogoutSuccessHandler");

        Cookie cookie = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);

        log.info("cookie value : " + cookie.getValue());

        cookie.setValue(null);

        log.info("set null cookie value : " + cookie.getValue());

        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

    }
}
