package com.board.boardapp.service;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.config.properties.JwtProperties;
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
    public JwtDTO checkExistsToken(HttpServletRequest request, HttpServletResponse response) {

        log.info("referer : " + request.getHeader("Referer"));

        Cookie at = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        /**
         * 리턴 조건.
         * 1. at가 null이지만 rt는 null이 아닐때      - F
         * 2. at와 rt 모두 null이 아닐때.            - T
         * 3. at와 rt 모두 null일때.                - N
         */

        if(at == null && rt != null)
            return reIssuedToken(request, response);
        else if(at != null && rt != null)
            return JwtDTO.builder()
                    .accessTokenHeader(at.getName())
                    .accessTokenValue(at.getValue())
                    .refreshTokenHeader(rt.getName())
                    .refreshTokenValue(rt.getValue())
                    .inoHeader(ino.getName())
                    .inoValue(ino.getValue())
                    .build();

        return null;
    }

    @Override
    public void saveToken(JwtDTO jwtDTO, HttpServletResponse response) {

        log.info("saveToken");

        //session을 활용하기 떄문에 lsc 쿠키는 더이상 필요가 없다.
        /*ResponseCookie lsc = ResponseCookie.from(JwtProperties.LSC_HEADER_STRING, UUID.randomUUID().toString())
                        .path("/")
                        .maxAge(JwtProperties.REFRESH_MAX_AGE)
                        .build();*/

        ResponseCookie at = createCookie(jwtDTO.getAccessTokenHeader(), jwtDTO.getAccessTokenValue(), JwtProperties.ACCESS_MAX_AGE);
        ResponseCookie rt = createCookie(jwtDTO.getRefreshTokenHeader(), jwtDTO.getRefreshTokenValue(), JwtProperties.REFRESH_MAX_AGE);


        response.addHeader("Set-Cookie", at.toString());
        response.addHeader("Set-Cookie", rt.toString());
//        response.addHeader("Set-Cookie", lsc.toString());


        //ino는 로그인시에는 생성되지만 갱신시에는 생성되지 않을것이기 때문에 매번 저장하면 안된다.
        if(jwtDTO.getInoHeader() != null) {
            ResponseCookie ino = createCookie(jwtDTO.getInoHeader(), jwtDTO.getInoValue(), JwtProperties.INO_MAX_AGE);
            response.addHeader("Set-Cookie", ino.toString());
        }

        log.info("save Token Success");

    }

    private ResponseCookie createCookie(String cookieHeader, String cookieValue, int maxAge) {
        return ResponseCookie.from(cookieHeader, cookieValue)
                            .path("/")
                            .maxAge(maxAge)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .build();
    }

    @Override
    public JwtDTO reIssuedToken(HttpServletRequest request, HttpServletResponse response) {

        WebClient client = clientConfig.useWebClient();
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        JwtDTO dto = client.post()
                            .uri(uriBuilder -> uriBuilder.path("/token/reissued").build())
                            .cookie(rt.getName(), rt.getValue())
                            .cookie(ino.getName(), ino.getValue())
                            .retrieve()
                            .bodyToMono(JwtDTO.class)
                            .block();

        log.info("dto : {} : {}", dto.getAccessTokenHeader(), dto.getAccessTokenValue());

        saveToken(dto, response);

        return dto;
    }

    @Override
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response) {

        for(String cookieHeader : JwtProperties.COOKIE_ARRAY) {
            Cookie cookie = WebUtils.getCookie(request, cookieHeader);

            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
