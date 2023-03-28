package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.JwtProperties;
import com.board.boardapp.dto.Member;
import com.board.boardapp.dto.MemberDTO;
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

    public void loginProc(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        log.info("restCall login");

        WebClient client = webClientConfig.useWebClient();

        Map<String, String> map = new HashMap<>();

        map.put("userId", request.getParameter("userId"));
        map.put("userPw", request.getParameter("userPw"));

        Member member = Member.builder()
                .userId(request.getParameter("userId"))
                .userPw(request.getParameter("userPw"))
                .build();

        JwtDTO responseVal = client.post()
                .uri(uriBuilder -> uriBuilder.path("/member/login").build())
                .bodyValue(member)
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
                .onStatus(
                        HttpStatus::isError, clientResponse ->
                                Mono.error(
                                        new AccessDeniedException("exception")
                                )
                )
                .bodyToMono(JwtDTO.class)
                .block();

        log.info("response : {}", responseVal);

        tokenService.saveToken(responseVal, response);

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

    public void logout(HttpServletRequest request, HttpServletResponse response){

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        WebClient client = webClientConfig.useWebClient();

        int responseVal = client.post()
                .uri(uriBuilder -> uriBuilder.path("/member/logout").build())
                .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
                .cookie(tokenDTO.getRefreshTokenHeader(), tokenDTO.getRefreshTokenValue())
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        log.info("logout response : {}", responseVal);
        /**
         * 로그아웃 처리가 정상적으로 동작했다면
         * lsc, Authorization, Authorization_Refresh 쿠키 전체 삭제.
         */

    }
}
