package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.ObjectReadValueService;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    private final WebClient client = new WebClientConfig().useWebClient();

    private final TokenService tokenService;

    private final ObjectReadValueService readValueService;

    private static final String commentPath = PathProperties.COMMENT_PATH;

    public CommentListDTO getBoardComment(long boardNo
                                            , Criteria cri) {

        String responseVal = client.get()
                                    .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-list")
                                            .queryParam("boardNo", String.valueOf(boardNo))
                                            .queryParam("pageNum", cri.getPageNum())
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


        CommentListDTO dto = new CommentListDTO();
        dto = readValueService.setReadValue(dto, responseVal);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    public CommentListDTO getImageComment(long imageNo
                                            , Criteria cri) {
        String responseVal = client.get()
                                    .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-list")
                                            .queryParam("imageNo", String.valueOf(imageNo))
                                            .queryParam("pageNum", cri.getPageNum())
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

        CommentListDTO dto = new CommentListDTO();
        dto = readValueService.setReadValue(dto, responseVal);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    public Long commentInsert(Map<String, Object> commentData
                                , HttpServletRequest request
                                , HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        Long imageNo = commentData.get("imageNo") == null ? null : Long.parseLong(commentData.get("imageNo").toString());
        Long boardNo = commentData.get("boardNo") == null ? null : Long.parseLong(commentData.get("boardNo").toString());

        CommentDTO dto = CommentDTO.builder()
                                    .commentContent(commentData.get("commentContent").toString())
                                    .imageNo(imageNo)
                                    .boardNo(boardNo)
                                    .build();

        Long result = client.post()
                        .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-insert").build())
                        .body(Mono.just(dto), CommentDTO.class)
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

        return result;
    }

    public Long commentReplyInsert(Map<String, Object> commentData
                                    , HttpServletRequest request
                                    , HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("AccessDined");

        String commentContent = commentData.get("commentContent").toString();
        long commentGroupNo = Long.parseLong(commentData.get("commentGroupNo").toString());
        int commentIndent = Integer.parseInt(commentData.get("commentIndent").toString());
        String commentUpperNo = commentData.get("commentUpperNo").toString();
        Long imageNo = commentData.get("imageNo") == null ? null : Long.parseLong(commentData.get("imageNo").toString());
        Long boardNo = commentData.get("boardNo") == null ? null : Long.parseLong(commentData.get("boardNo").toString());

        CommentDTO dto = CommentDTO.builder()
                                    .commentContent(commentContent)
                                    .commentGroupNo(commentGroupNo)
                                    .commentIndent(commentIndent)
                                    .commentUpperNo(commentUpperNo)
                                    .imageNo(imageNo)
                                    .boardNo(boardNo)
                                    .build();

        Long result = client.post()
                .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-reply").build())
                .body(Mono.just(dto), CommentDTO.class)
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


        return result;
    }

    public Long commentDelete(long commentNo, HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("AccessDenied");

        Long result = client.delete()
                .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-delete/{commentNo}")
                        .build(commentNo))
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

        return result;
    }
}
