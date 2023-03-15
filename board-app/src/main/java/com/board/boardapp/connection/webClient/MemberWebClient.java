package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.Member;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
                .bodyToMono(JwtDTO.class)
                .block();

        log.info("response : {}", responseVal);

        tokenService.saveToken(responseVal, response);

    }
}
