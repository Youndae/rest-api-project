package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.WebUtils;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberWebClient {

    private final WebClientConfig webClientConfig;

    private final TokenService tokenService;

    public int loginProc(Map<String, String> loginData
            , HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        log.info("restCall login");

        WebClient client = webClientConfig.useWebClient();

        Cookie ino = WebUtils.getCookie(request, JwtProperties.INO_HEADER_STRING);

        Member member = Member.builder()
                .userId(loginData.get("userId"))
                .userPw(loginData.get("userPw"))
                .build();

        JwtDTO responseVal = null;

        if(ino != null){
            responseVal = client.post()
                    .uri(uriBuilder -> uriBuilder.path("/member/login").build())
                    .cookie(ino.getName(), ino.getValue())
                    .bodyValue(member)
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError, clientResponse ->
                                    Mono.error(
                                            new CustomNotFoundException(ErrorCode.USER_NOT_FOUND)
                                    )
                    )
                    .bodyToMono(JwtDTO.class)
                    .block();
        }else{
            responseVal = client.post()
                    .uri(uriBuilder -> uriBuilder.path("/member/login").build())
                    .bodyValue(member)
                    .retrieve()
                    .onStatus(
                            HttpStatus::is4xxClientError, clientResponse ->
                                    Mono.error(
                                            new CustomNotFoundException(ErrorCode.USER_NOT_FOUND)
                                    )
                    )
                    .bodyToMono(JwtDTO.class)
                    .block();
        }

        log.info("response : {}", responseVal);

        if(responseVal != null){
            tokenService.saveToken(responseVal, response);
            return 1;
        }else{
            return 0;
        }

    }

    public int checkUserId(String userId){

        return webClientConfig.useWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/member/check-user-id")
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
                .bodyToMono(Integer.class)
                .block();
    }

    public int joinProc(MemberDTO dto){

        return webClientConfig.useWebClient().post()
                .uri(uriBuilder -> uriBuilder.path("/member/join-proc").build())
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
                .bodyToMono(Integer.class)
                .block();

    }

    public int logout(HttpServletRequest request, HttpServletResponse response){

        log.info("logout webClient");

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        WebClient client = webClientConfig.useWebClient();

        return client.post()
                .uri(uriBuilder -> uriBuilder.path("/member/logout").build())
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
                                        new NullPointerException()
                                )
                )
                .bodyToMono(Integer.class)
                .block();
    }
}
