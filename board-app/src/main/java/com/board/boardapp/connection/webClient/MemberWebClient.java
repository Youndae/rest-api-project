package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
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
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
}
