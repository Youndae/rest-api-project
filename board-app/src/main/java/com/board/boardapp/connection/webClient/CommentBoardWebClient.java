package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.controller.ExceptionHandlerAdvice;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentBoardWebClient {

    private final WebClientConfig webClientConfig;

    private final TokenService tokenService;

    public CommentListDTO getBoardComment(long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response
                                            , Criteria cri) throws JsonProcessingException {
        WebClient client = webClientConfig.useWebClient();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        String responseVal = "";

        log.info("pageNum : {}, amount : {}", cri.getPageNum(), cri.getBoardAmount());

        if(tokenDTO == null){
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/comment/comment-list")
                            .queryParam("boardNo", String.valueOf(boardNo))
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getBoardAmount())
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
        }else if(tokenDTO != null){
            log.info("token is not null");
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/comment/comment-list")
                            .queryParam("boardNo", String.valueOf(boardNo))
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getBoardAmount())
                            .build())
                    .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
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

        log.info("boardComment responseVal : {}", responseVal);

        CommentListDTO dto;

        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        dto = om.readValue(responseVal, CommentListDTO.class);

        log.info("boardComment TotalPages : {}", dto.getTotalPages());

        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    public int commentInsert(Map<String, Object> commentData
                                , HttpServletRequest request
                                , HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            new AccessDeniedException("Denied Exception");
        }

        WebClient client = webClientConfig.useWebClient();


        CommentDTO dto;

        if(commentData.get("boardNo") == null){

            dto = CommentDTO.builder()
                    .commentContent(commentData.get("commentContent").toString())
                    .imageNo(Long.parseLong(commentData.get("imageNo").toString()))
                    .build();
        }else{
            dto = CommentDTO.builder()
                    .commentContent(commentData.get("commentContent").toString())
                    .boardNo(Long.parseLong(commentData.get("boardNo").toString()))
                    .build();
        }
        log.info("comment Insert Service");

        int result = client.post()
                        .uri(uriBuilder -> uriBuilder.path("/comment/comment-insert").build())
                        .body(Mono.just(dto), CommentDTO.class)
                        .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
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

        log.info("result : {}", result);


        return result;
    }

    public int commentReplyInsert(Map<String, Object> commentData
                                    , HttpServletRequest request
                                    , HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            return -1;
        }

        WebClient client = webClientConfig.useWebClient();

        String commentContent = commentData.get("commentContent").toString();
        long commentGroupNo = Long.parseLong(commentData.get("commentGroupNo").toString());
        int commentIndent = Integer.parseInt(commentData.get("commentIndent").toString());
        String commentUpperNo = commentData.get("commentUpperNo").toString();

        CommentDTO dto;

        if(commentData.get("boardNo") == null){
            dto = CommentDTO.builder()
                    .commentContent(commentContent)
                    .commentGroupNo(commentGroupNo)
                    .commentIndent(commentIndent)
                    .commentUpperNo(commentUpperNo)
                    .imageNo(Long.parseLong(commentData.get("imageNo").toString()))
                    .build();
        }else{
            dto = CommentDTO.builder()
                    .commentContent(commentContent)
                    .commentGroupNo(commentGroupNo)
                    .commentIndent(commentIndent)
                    .commentUpperNo(commentUpperNo)
                    .boardNo(Long.parseLong(commentData.get("boardNo").toString()))
                    .build();
        }

        int result = client.post()
                .uri(uriBuilder -> uriBuilder.path("/comment/comment-reply").build())
                .body(Mono.just(dto), CommentDTO.class)
                .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
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

        log.info("reply result : {}", result);

        return result;
    }

    public int commentDelete(long commentNo, HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            return -1;

        WebClient client = webClientConfig.useWebClient();

        int result = client.delete()
                .uri(uriBuilder -> uriBuilder.path("/comment/comment-delete/{commentNo}")
                        .build(commentNo))
                .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
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

        log.info("commentDelete client result : {}", result);

        return result;
    }
}
