package com.board.boardapp.connection.webClient;

import com.board.boardapp.ExceptionHandle.CustomBadCredentialsException;
import com.board.boardapp.ExceptionHandle.CustomTokenStealingException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.domain.dto.JoinDTO;
import com.board.boardapp.domain.dto.Member;
import com.board.boardapp.domain.dto.ProfileDTO;
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

    public String loginProc(Map<String, String> loginData
                        , MultiValueMap<String, String> cookieMap
                        , HttpServletResponse response) {
        Member member = Member.builder()
                .userId(loginData.get("userId"))
                .userPw(loginData.get("userPw"))
                .build();

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "login").build())
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .bodyValue(member)
                        .exchangeToMono(res -> {

                            if(res.statusCode().equals(HttpStatus.OK)){
                                cookieService.setCookie(res, response);
                            }else if(res.statusCode().equals(HttpStatus.FORBIDDEN)){
                                throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());
                            }else if(res.rawStatusCode() == 800){
                                cookieService.setCookie(res, response);
                                throw new CustomTokenStealingException(ErrorCode.TOKEN_STEALING);
                            }else if(res.statusCode().is4xxClientError()){
                                throw new RuntimeException();
                            }else if(res.statusCode().is5xxServerError()){
                                throw new NullPointerException();
                            }

                            return res.bodyToMono(String.class);
                        })
                        .block();

    }

    public String checkUserId(String userId){

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
                        .bodyToMono(String.class)
                        .block();
    }

    public String joinProc(JoinDTO dto, MultipartFile profileThumbnail){
        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();
        mbBuilder.part("joinDTO", dto);

        if(profileThumbnail != null){
            String sizeCheckResult = profileThumbnailCheck(profileThumbnail);

            if(sizeCheckResult.equals("SIZE"))
                return sizeCheckResult;

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
                .bodyToMono(String.class)
                .block();
    }

    public String logout(MultiValueMap<String, String> cookieMap, HttpServletResponse response){

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(memberPath + "logout").build())
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .acceptCharset(Charset.forName("UTF-8"))
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(String.class);
                        })
                        .block();
    }

    public String checkNickname(String nickname, MultiValueMap<String, String> cookieMap) {

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
                .bodyToMono(String.class)
                .block();
    }

    public ProfileDTO getProfile(MultiValueMap<String, String> cookieMap, HttpServletResponse response) {

        ProfileDTO dto = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "profile").build())
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(ProfileDTO.class);
                })
                .block();

        dto.setStatusAndBtn();

        return dto;
    }

    public String patchProfile(String nickname
                            , MultipartFile profileThumbnail
                            , String deleteProfileThumbnail
                            , MultiValueMap<String, String> cookieMap
                            , HttpServletResponse response) {

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();
        mbBuilder.part("nickname", nickname);

        if(profileThumbnail != null){
            String sizeCheckResult = profileThumbnailCheck(profileThumbnail);

            if(sizeCheckResult.equals("SIZE"))
                return sizeCheckResult;

            mbBuilder.part("profileThumbnail", profileThumbnail.getResource());
        }

        if(deleteProfileThumbnail != null)
            mbBuilder.part("deleteProfile", deleteProfileThumbnail);

        return imageWebClient.patch()
                .uri(uriBuilder -> uriBuilder.path(memberPath + "profile").build())
                .cookies(cookies -> cookies.addAll(cookieMap))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();
    }

    public String profileThumbnailCheck(MultipartFile profileThumbnail) {

        List<MultipartFile> profileThumbnailList = new ArrayList<>();
        profileThumbnailList.add(profileThumbnail);
        if(imageBoardWebClient.imageSizeCheck(profileThumbnailList) == -2) {
            log.info("sizeCheck Fail");
            return "SIZE";
        }

        return "OK";
    }
}
