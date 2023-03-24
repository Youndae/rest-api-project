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

        log.info("request url : " + request.getRequestURL());

        /**
         * referer 체크를 하는데
         * 특정 url(post, patch, delete) 요청이나 사용자 정보 같은 중요 정보를 get 요청 하는 경우에는
         * 이전 url을 localhost:8080/** 형태가 아닌 아예 버튼 누르기 전 이전 url을 Referer로 갖고 있는지 체크.
         * 굳이 이렇게 까지 해야하나 싶었었는데 확실하게 차단하기 위해서는 이렇게 하는게 낫다고 생각.
         * 더 좋은 방법이 있을 수 있겠지만 현재로서는 이게 최선인듯.
         */

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
