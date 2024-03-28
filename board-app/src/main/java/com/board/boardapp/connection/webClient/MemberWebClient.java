package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.ExchangeService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final ExchangeService exchangeService;

    private final CookieService cookieService;

    private static final String memberPath = PathProperties.MEMBER_PATH;

    public Long loginProc(Map<String, String> loginData
                        , HttpServletRequest request
                        , HttpServletResponse response) {
        String path = memberPath + "/login";

        Member member = Member.builder()
                .userId(loginData.get("userId"))
                .userPw(loginData.get("userPw"))
                .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(path).build())
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .bodyValue(member)
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(Long.class);
                        })
                        .block();

    }

    public Long checkUserId(String userId){

        return webClient.get()
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

        return webClient.post()
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
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "/logout").build())
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .acceptCharset(Charset.forName("UTF-8"))
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(Long.class);
                        })
                        .block();
    }
}
