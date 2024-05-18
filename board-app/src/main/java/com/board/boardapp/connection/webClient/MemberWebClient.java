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
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final WebClient imageWebClient = new WebClientConfig().useImageWebClient();

    private final ExchangeService exchangeService;

    private final CookieService cookieService;

    private static final String memberPath = PathProperties.MEMBER_PATH;

    private final ImageBoardWebClient imageBoardWebClient;

    public Long loginProc(Map<String, String> loginData
                        , HttpServletRequest request
                        , HttpServletResponse response) {
        Member member = Member.builder()
                .userId(loginData.get("userId"))
                .userPw(loginData.get("userPw"))
                .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "login").build())
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
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "check-id")
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

    public Long joinProc(JoinDTO dto, MultipartFile profileThumbnail){
        log.info("joinProc");

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();
        mbBuilder.part("joinDTO", dto);


        if(profileThumbnail != null){
            log.info("profile is not empty");
            List<MultipartFile> profileThumbnailList = new ArrayList<>();
            profileThumbnailList.add(profileThumbnail);
            if(imageBoardWebClient.imageSizeCheck(profileThumbnailList) == -2) {
                log.info("sizecheck Fail");
                return -1L;
            }
            log.info("sizeCheckSuccess");

            mbBuilder.part("profileThumbnail", profileThumbnail.getResource());
        }



        return imageWebClient.post()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "join").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mbBuilder.build()))
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


        /*return joinWebClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "join").build())
                        .accept()
                        .body(Mono.just(dto), JoinDTO.class)
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
                        .block();*/
    }

    public Long logout(HttpServletRequest request, HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "logout").build())
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .acceptCharset(Charset.forName("UTF-8"))
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(Long.class);
                        })
                        .block();
    }

    public Long checkNickname(String nickname, HttpServletRequest request) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "check-nickname")
                        .queryParam("nickname", nickname)
                        .build())
                .cookies(cookies -> cookies.addAll(cookieMap))
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

    public ProfileDTO getProfile(HttpServletRequest request, HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "profile").build())
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(ProfileDTO.class);
                })
                .block();
    }

    public Long patchProfile(String nickname, MultipartFile profileThumbnail, String deleteProfileThumbnail, HttpServletRequest request, HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();
        mbBuilder.part("nickname", nickname);

        if(profileThumbnail != null)
            mbBuilder.part("profileThumbnail", profileThumbnail.getResource());

        if(deleteProfileThumbnail != null)
            mbBuilder.part("deleteProfile", deleteProfileThumbnail);

        return imageWebClient.patch()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "profile").build())
                .cookies(cookies -> cookies.addAll(cookieMap))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();
    }
}
