package com.board.boardapp.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RefererInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("TokenInterceptor");

        log.info("TokenInterceptor referer : " + request.getHeader("Referer"));


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
