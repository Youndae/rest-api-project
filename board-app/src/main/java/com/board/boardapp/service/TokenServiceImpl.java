package com.board.boardapp.service;

import com.board.boardapp.config.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    @Override
    public boolean checkExistsToken(HttpServletRequest request) {
        Cookie at = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);
        Cookie rt = WebUtils.getCookie(request, JwtProperties.REFRESH_HEADER_STRING);
        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        /**
         * 단지 insert 페이지 접근을 위한 체크이기 때문에 모든 쿠키가 존재하는지만 체크.
         * 만약 모든 쿠키가 존재하지 않는다면 토큰 탈취이거나, 토큰 만료일 것이기 때문에
         * 접근을 허용할 필요가 없음.
         *
         * 보안 역시 insert 페이지 접근만 허용하고 이후 POST 요청에 대해서는 서버에서 토큰 검증을 수행할 것이기 때문에
         * 접근은 존재여부로만 허용해도 되겠다고 판단.
         *
         * 모든 쿠키가 존재한다면 true를 반환
         */

        if(at != null && rt != null && ino != null)
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
