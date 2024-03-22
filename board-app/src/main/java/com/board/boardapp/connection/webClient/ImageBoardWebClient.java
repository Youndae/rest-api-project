package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardWebClient {

    private final WebClient client = new WebClientConfig().useWebClient();

    private final WebClient imageClient = new WebClientConfig().useImageWebClient();

//    private final TokenService tokenService;

    private final ObjectReadValueService readValueService;

    private final CookieService cookieService;

    private final ExchangeService exchangeService;

    private static final String imagePath = PathProperties.IMAGE_BOARD_PATH;

    public ImageBoardListDTO getImageBoardList(Criteria cri, HttpServletRequest request, HttpServletResponse response) {
        String path = imagePath + "/image-board-list";

        UriComponents ub = UriComponentsBuilder.newInstance()
                                                .path(path)
                                                .queryParam("pageNum", cri.getPageNum())
                                                .build();

        if(cri.getKeyword() != null)
            ub = UriComponentsBuilder.newInstance()
                                    .path(path)
                                    .queryParam("pageNum", cri.getPageNum())
                                    .queryParam("keyword", cri.getKeyword())
                                    .queryParam("searchType", cri.getSearchType())
                                    .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*String result = client.get()
                                .uri(ub.toUriString())
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
                                .block();*/


        String result = client.get()
                                .uri(ub.toUriString())
                                .cookies(cookies -> cookies.addAll(cookieMap))
                                .exchangeToMono(res -> {
                                    exchangeService.checkExchangeResponse(res, response);

                                    return res.bodyToMono(String.class);
                                })
                                .block();

        /*if(result == null)
            new Exception();*/

        ImageBoardListDTO dto = new ImageBoardListDTO();
        dto = readValueService.setReadValue(dto, result);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    public byte[] getImageDisplay(String imageName){

        /**
         * 메모리 초과 오류 발생.
         * 버퍼 사이즈를 추가한 메소드를 별도로 만들어서 처리.
         * 문제 해결 도움된 블로그 url
         * https://flyburi.com/617
         */
        return imageClient.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/display")
                        .queryParam("imageName", imageName)
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
                .bodyToMono(byte[].class)
                .block();
    }

    public BoardDetailAndModifyDTO<ImageBoardDetailDTO> getImageDetail(long imageNo
                                                , HttpServletRequest request
                                                , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*String responseVal = client.get()
                                    .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-board-detail/{imageNo}")
                                            .build(imageNo))
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
                                    .block();*/

        String responseVal = client.get()
                                    .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-board-detail/{imageNo}")
                                            .build(imageNo))
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

//        ImageBoardDetailDTO dto = new ImageBoardDetailDTO();
        BoardDetailAndModifyDTO<ImageBoardDetailDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    public Long imageBoardInsert(String imageTitle
                                , String imageContent
                                , List<MultipartFile> files
                                , HttpServletRequest request
                                , HttpServletResponse response){

//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        if(imageSizeCheck(files) == -2L)
            return -2L;

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        files.stream().forEach(file -> {
            mbBuilder.part("files", file.getResource());
        });

        mbBuilder.part("imageTitle", imageTitle);
        mbBuilder.part("imageContent", imageContent);

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);


        /*return imageClient.post()
                        .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-insert").build())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(mbBuilder.build()))
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
                        .bodyToMono(Long.class)
                        .block();*/

        return imageClient.post()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-insert").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();

    }

    // 이미지파일 사이즈 체크
    public long imageSizeCheck(List<MultipartFile> images) {
        for(MultipartFile image : images){
            if(image.getSize() >= 10 * 1024 * 1024){
                log.info("image size is larger than the limit size");
                return -2;
            }
        }

        return 1;
    }

    public BoardDetailAndModifyDTO<ImageBoardDTO> getModifyData(long imageNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response){
//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*ImageBoardDTO dto = client.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/modify-data/{imageNo}").build(imageNo))
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
                .bodyToMono(ImageBoardDTO.class)
                .block();*/

        String result = client.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/modify-data/{imageNo}").build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

        BoardDetailAndModifyDTO<ImageBoardDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, result);

        return dto;
    }

    public List<ImageDataDTO> getModifyImageList(long imageNo
                                                    , HttpServletRequest request
                                                    , HttpServletResponse response) throws JsonProcessingException {
        /*JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");*/

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

       /*String responseVal = client.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/modify-image-attach/{imageNo}").build(imageNo))
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
                .bodyToMono(String.class)
                .block();*/

        String responseVal = client.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/modify-image-attach/{imageNo}").build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

       List<ImageDataDTO> dto = new ArrayList<>();
       dto = readValueService.setReadValue(dto, responseVal);

       return dto;
    }

    public Long imageBoardModify(long imageNo, String imageTitle, String imageContent
                                    , List<MultipartFile> files, List<String> deleteFiles
                                    , HttpServletRequest request, HttpServletResponse response){
        /*JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");*/

        if(files.size() > 0 && imageSizeCheck(files) == -2L)
            return -2L;

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        Optional.ofNullable(files)
                        .orElseGet(Collections::emptyList)
                        .forEach(file -> {
                            mbBuilder.part("files", file.getResource());
                        });

        Optional.ofNullable(deleteFiles)
                .orElseGet(Collections::emptyList)
                .forEach(file -> {
                    mbBuilder.part("files", file);
                });

        mbBuilder.part("imageTitle", imageTitle);
        mbBuilder.part("imageContent", imageContent);
        mbBuilder.part("imageNo", imageNo);

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*return imageClient.patch()
                        .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-modify").build())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                        .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
                        .cookie(tokenDTO.getRefreshTokenHeader(), tokenDTO.getRefreshTokenValue())
                        .cookie(tokenDTO.getInoHeader(), tokenDTO.getInoValue())
                        .retrieve()
                        .onStatus(
                                HttpStatus::is4xxClientError, clientResponse ->
                                        Mono.error(new NotFoundException("not found"))
                        )
                        .onStatus(
                                HttpStatus::is5xxServerError, clientResponse ->
                                        Mono.error(new NullPointerException())
                        )
                        .bodyToMono(Long.class)
                        .block();*/

        return imageClient.patch()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-modify").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();
    }

    public Long imageBoardDelete(long imageNo, HttpServletRequest request, HttpServletResponse response) {
        /*JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");*/

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*try{
            return client.delete()
                    .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-delete/{imageNo}").build(imageNo))
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
                    .bodyToMono(Long.class)
                    .block();

        }catch (Exception e){
            return 0L;
        }*/

        return client.delete()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/image-delete/{imageNo}").build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();
    }

}
