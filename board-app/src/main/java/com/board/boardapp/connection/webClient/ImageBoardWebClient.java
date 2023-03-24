package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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

        int amount = 12;

        String response = null;

        if(cri.getKeyword() == null || cri.getKeyword().equals("")){
            log.info("keyword is null");
            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getAmount())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }else if(cri.getKeyword() != null || !cri.getKeyword().equals("")){
            log.info("keyword is not null");
            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getAmount())
                            .queryParam("keyword", cri.getKeyword())
                            .queryParam("searchType", cri.getSearchType())
                            .build())
                    .retrieve()
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
                    .bodyToMono(String.class)
                    .block();
        }else{
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/image-board/image-board-detail/{imageNo}")
                            .build(imageNo))
                    .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
                    .retrieve()
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
                .retrieve()
                .bodyToMono(Long.class)
                .block();

//        log.info("result : {}", result);

//         return 1;
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
                .retrieve()
                .bodyToMono(ImageBoardDTO.class)
                .block();

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
                .retrieve()
                .bodyToMono(String.class)
                .block();

       ObjectMapper om = new ObjectMapper();

       om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

       List<ImageDataDTO> dto = om.readValue(responseVal, List.class);

       for(int i = 0; i < dto.size(); i++){
           log.info("dto : {}", dto.get(i));
       }

       return dto;
    }

    public long imageBoardModify(long imageNo
                                    , String imageTitle
                                    , String imageContent
                                    , List<MultipartFile> files
                                    , List<String> deleteFiles
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        WebClient client = webClientConfig.useImageWebClient();

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();



        if(files != null){
            log.info("files is not null");
            for(MultipartFile file : files){
                log.info("file.originName : {}", file.getOriginalFilename());
                mbBuilder.part("files", file.getResource());
            }
        }

        if(deleteFiles != null){
            log.info("delete file is not null");
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
                .retrieve()
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
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();

        }catch (Exception e){
            return 0;
        }
    }

}
