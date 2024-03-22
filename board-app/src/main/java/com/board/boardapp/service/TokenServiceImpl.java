package com.board.boardapp.service;

import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.config.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final WebClientConfig clientConfig;

    @Override
    public boolean checkExistsToken(HttpServletRequest request) {

        log.info("referer : " + request.getHeader("Referer"));

//        Cookie at = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        /**
         * 리턴 조건.
         * 1. at가 null이지만 rt는 null이 아닐때      - F
         * 2. at와 rt 모두 null이 아닐때.            - T
         * 3. at와 rt 모두 null일때.                - N
         */

        /*if(at == null && rt != null)
            return reIssuedToken(request, response);
        else if(at != null && rt != null)
            return JwtDTO.builder()
                    .accessTokenHeader(at.getName())
                    .accessTokenValue(at.getValue())
                    .refreshTokenHeader(rt.getName())
                    .refreshTokenValue(rt.getValue())
                    .inoHeader(ino.getName())
                    .inoValue(ino.getValue())
                    .build();*/

        /**
         * 단지 insert 페이지 접근을 위한 체크이기 때문에 rt, ino가 존재하는지만 체크.
         * 둘다 존재하는 경우에만 true를 반환해 접근을 허용.
         */

        if(rt != null && ino != null)
            return true;

        return false;
    }

    /*@Override
    public JwtDTO reIssuedToken(HttpServletRequest request, HttpServletResponse response) {

        WebClient client = clientConfig.useWebClient();
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        JwtDTO dto = client.post()
                .uri(uriBuilder -> uriBuilder.path("/token/reissued").build())
                .cookie(rt.getName(), rt.getValue())
                .cookie(ino.getName(), ino.getValue())
                .acceptCharset(Charset.forName("UTF-8"))
                .exchangeToMono(res -> {
                    if(res.statusCode().equals(HttpStatus.OK)){
                        res.cookies()
                                .forEach((k, v) ->
                                        response.addHeader("Set-Cookie", v.get(0).toString())
                                );
                    }else if(res.statusCode().is4xxClientError())
                        new CustomNotFoundException(ErrorCode.REISSUED_ERROR);

                    return res.bodyToMono(JwtDTO.class);
                })
                .block();

        return dto;
    }*/
}
