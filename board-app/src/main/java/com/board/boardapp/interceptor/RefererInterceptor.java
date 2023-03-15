package com.board.boardapp.interceptor;

import com.board.boardapp.dto.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class RefererInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("TokenInterceptor");

        log.info("TokenInterceptor referer : " + request.getHeader("Referer"));


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
