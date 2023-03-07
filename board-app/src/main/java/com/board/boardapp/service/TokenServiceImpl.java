package com.board.boardapp.service;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final WebClientConfig clientConfig;

    @Override
    public String checkExistsToken(HttpServletRequest request) {

        log.info("referer : " + request.getHeader("Referer"));

        Cookie at = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        /**
         * 리턴 조건.
         * 1. at가 null이지만 rt는 null이 아닐때      - F
         * 2. at와 rt 모두 null이 아닐때.            - T
         * 3. at와 rt 모두 null일때.                - N
         */

        if(at == null && rt != null)
            return "F";
        else if(at != null && rt != null)
            return "T";
        else if(at == null && rt == null)
            return "N";

        return "error";

    }

    @Override
    public void saveToken(JwtDTO jwtDTO, HttpServletResponse response) {

        log.info("saveToken");

        ResponseCookie at = ResponseCookie.from(jwtDTO.getAccessTokenHeader(), jwtDTO.getAccessTokenValue())
                .path("/")
                .maxAge(JwtProperties.ACCESS_MAX_AGE)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        ResponseCookie rt = ResponseCookie.from(jwtDTO.getRefreshTokenHeader(), jwtDTO.getRefreshTokenValue())
                .path("/")
                .maxAge(JwtProperties.REFRESH_MAX_AGE)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", at.toString());
        response.addHeader("Set-Cookie", rt.toString());

        log.info("save Token Success");

    }

    @Override
    public JwtDTO reIssuedToken(HttpServletRequest request, HttpServletResponse response) {

        WebClient client = clientConfig.useWebClient();
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);

        JwtDTO dto = client.post()
                .uri(uriBuilder -> uriBuilder.path("reissuedToken").build())
                .cookie(rt.getName(), rt.getValue())
                .retrieve()
                .bodyToMono(JwtDTO.class)
                .block();

        saveToken(dto, response);

        return dto;
    }
}
