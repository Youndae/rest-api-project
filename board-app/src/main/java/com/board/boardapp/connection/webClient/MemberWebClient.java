package com.board.boardapp.connection.webClient;

import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.JwtProperties;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.WebUtils;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberWebClient {

    private final WebClient client = new WebClientConfig().useWebClient();

    private final TokenService tokenService;

    private static final String memberPath = PathProperties.MEMBER_PATH;

    public int loginProc(Map<String, String> loginData
                        , HttpServletRequest request
                        , HttpServletResponse response) {

        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        String path = memberPath + "/login";

        Member member = Member.builder()
                .userId(loginData.get("userId"))
                .userPw(loginData.get("userPw"))
                .build();

        Long responseVal = null;

        if(ino != null){
            responseVal = client.post()
                    .uri(uriBuilder -> uriBuilder.path(path).build())
                    .cookie(ino.getName(), ino.getValue())
                    .acceptCharset(Charset.forName("UTF-8"))
                    .bodyValue(member)
                    .exchangeToMono(resp -> {
                        if(resp.statusCode().equals(HttpStatus.OK)) {
                            System.out.println("login Success");
                            resp.cookies()
                                    .forEach((k, v) ->
                                            response.addHeader("Set-Cookie", v.get(0).toString()
                                            ));
                        }else if(resp.statusCode().is4xxClientError())
                            new CustomNotFoundException(ErrorCode.USER_NOT_FOUND);

                        return resp.bodyToMono(Long.class);
                    })
                    .block();
        }else {
            responseVal = client.post()
                    .uri(uriBuilder -> uriBuilder.path(path).build())
                    .acceptCharset(Charset.forName("UTF-8"))
                    .bodyValue(member)
                    .exchangeToMono(resp -> {
                        if(resp.statusCode().equals(HttpStatus.OK)) {
                            System.out.println("login Success");
                            resp.cookies()
                                    .forEach((k, v) ->
                                            response.addHeader("Set-Cookie", v.get(0).toString()
                                            ));
                        }else if(resp.statusCode().is4xxClientError())
                            new CustomNotFoundException(ErrorCode.USER_NOT_FOUND);

                        return resp.bodyToMono(Long.class);
                    })
                    .block();
        }

        if(responseVal == 1L){
            HttpSession session = request.getSession();
            session.setAttribute("id", member.getUserId());

            return 1;
        }else{
            return 0;
        }

    }

    public Long checkUserId(String userId){

        return client.get()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "/check-user-id")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, clientResponse ->
                                Mono.error(
                                        new NotFoundException("not found")
                                )
                )
                .onStatus(
                        HttpStatus::is5xxServerError, clientResponse ->
                                Mono.error(
                                        new NullPointerException()
                                )
                )
                .bodyToMono(Long.class)
                .block();
    }

    public Long joinProc(MemberDTO dto){

        return client.post()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "/join-proc").build())
                .accept()
                .body(Mono.just(dto), MemberDTO.class)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, clientResponse ->
                                Mono.error(
                                        new NotFoundException("not found")
                                )
                )
                .onStatus(
                        HttpStatus::is5xxServerError, clientResponse ->
                                Mono.error(
                                        new NullPointerException()
                                )
                )
                .bodyToMono(Long.class)
                .block();

    }

    public Long logout(HttpServletRequest request, HttpServletResponse response){
        log.info("logout webClient");
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        Long result = client.post()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "/logout").build())
                .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
                .cookie(tokenDTO.getRefreshTokenHeader(), tokenDTO.getRefreshTokenValue())
                .cookie(tokenDTO.getInoHeader(), tokenDTO.getInoValue())
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, clientResponse ->
                                Mono.error(
                                        new NotFoundException("not found")
                                )
                )
                .onStatus(
                        HttpStatus::is5xxServerError, clientResponse ->
                                Mono.error(
                                        new NullPointerException("NullPointerException 발생!")
                                )
                )
                .bodyToMono(Long.class)
                .block();


        if(result == 1L) {
            //세션 제거
            HttpSession session = request.getSession();
            session.invalidate();
            //토큰 쿠키 제거
            tokenService.deleteCookie(request, response);
        }

        return result;
    }
}
