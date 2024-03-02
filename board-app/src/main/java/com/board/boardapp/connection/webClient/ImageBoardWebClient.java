package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardWebClient {

    private final WebClientConfig webClientConfig;

    private final TokenService tokenService;

    public ImageBoardListDTO getImageBoardList(Criteria cri) throws JsonProcessingException{

        WebClient client = webClientConfig.useWebClient();

        String response = null;

        if(cri.getKeyword() == null || cri.getKeyword().equals("")){
            log.info("keyword is null");
            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getImageAmount())
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
        }else if(cri.getKeyword() != null || !cri.getKeyword().equals("")){
            log.info("keyword is not null");
            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getImageAmount())
                            .queryParam("keyword", cri.getKeyword())
                            .queryParam("searchType", cri.getSearchType())
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

        ObjectMapper om = new ObjectMapper();

        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ImageBoardListDTO dto;

        if(response == null){
            new Exception();

        }

        dto = om.readValue(response, ImageBoardListDTO.class);

        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        log.info("dto : {}", dto);

        return dto;
    }

    public byte[] getImageDisplay(String imageName){

        /**
         * 메모리 초과 오류 발생.
         * 버퍼 사이즈를 추가한 메소드를 별도로 만들어서 처리.
         * 문제 해결 도움된 블로그 url
         * https://flyburi.com/617
         */
        WebClient client = webClientConfig.useImageWebClient();

        log.info("getImageDisplay client");
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/image-board/display").queryParam("imageName", imageName).build())
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

    public ImageBoardDetailDTO getImageDetail(long imageNo
                                                , HttpServletRequest request
                                                , HttpServletResponse response) throws JsonProcessingException {

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        String responseVal = null;

        WebClient client = webClientConfig.useWebClient();

        if(tokenDTO == null){
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-detail/{imageNo}")
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
                    .block();
        }else{
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-detail/{imageNo}")
                            .build(imageNo))
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
                    .block();
        }

        log.info("imageDetail response value : {}", responseVal);

        ObjectMapper om = new ObjectMapper();

        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ImageBoardDetailDTO dto = om.readValue(responseVal, ImageBoardDetailDTO.class);

        return dto;
    }

    public long imageBoardInsert(String imageTitle
                                , String imageContent
                                , List<MultipartFile> files
                                , HttpServletRequest request
                                , HttpServletResponse response){

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useImageWebClient();
        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        /*files.stream().forEach(file -> {
            mbBuilder.part("files", file.getResource());
        });*/

        for(int i = 0; i < files.size(); i++){
            mbBuilder.part("files", files.get(i).getResource());
        }
        mbBuilder.part("imageTitle", imageTitle);
        mbBuilder.part("imageContent", imageContent);


        return client.post()
                .uri(uriBuilder -> uriBuilder.path("/image-board/image-insert").build())
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
                .block();

    }

    public ImageBoardDTO getModifyData(long imageNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useWebClient();

        ImageBoardDTO dto = client.get()
                .uri(uriBuilder -> uriBuilder.path("/image-board/modify-data/{imageNo}").build(imageNo))
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
                .block();

        log.info("getModifyData return dto : {}", dto);

        return dto;
    }

    public List<ImageDataDTO> getModifyImageList(long imageNo
                                                    , HttpServletRequest request
                                                    , HttpServletResponse response) throws JsonProcessingException {
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useImageWebClient();

       String responseVal = client.get()
                .uri(uriBuilder -> uriBuilder.path("/image-board/modify-image-attach/{imageNo}").build(imageNo))
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
                .block();

       ObjectMapper om = new ObjectMapper();

       om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

       List<ImageDataDTO> dto = om.readValue(responseVal, List.class);

       return dto;
    }

    public Long imageBoardModify(long imageNo, String imageTitle, String imageContent
                                    , List<MultipartFile> files, List<String> deleteFiles
                                    , HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useImageWebClient();
        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        /*files.stream().forEach(file -> {
            mbBuilder.part("files", file.getResource());
        });

        deleteFiles.stream().forEach(file -> {
            mbBuilder.part("deleteFiles", file);
        });*/

        if(files != null){
            for(MultipartFile file : files){
                mbBuilder.part("files", file.getResource());
            }
        }

        if(deleteFiles != null){
            for(String file : deleteFiles){
                mbBuilder.part("deleteFiles", file);
            }
        }

        mbBuilder.part("imageTitle", imageTitle);
        mbBuilder.part("imageContent", imageContent);
        mbBuilder.part("imageNo", imageNo);

        return client.patch()
                .uri(uriBuilder -> uriBuilder.path("/image-board/image-modify").build())
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
                .block();
    }

    public long imageBoardDelete(long imageNo, HttpServletRequest request, HttpServletResponse response) {
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useWebClient();

        try{
            return client.delete()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-delete/{imageNo}").build(imageNo))
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
            return 0;
        }
    }

}
