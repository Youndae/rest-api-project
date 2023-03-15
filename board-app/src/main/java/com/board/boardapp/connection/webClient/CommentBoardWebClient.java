package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.dto.CommentListDTO;
import com.board.boardapp.dto.Criteria;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.PageDTO;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        log.info("pageNum : {}, amount : {}", cri.getPageNum(), cri.getAmount());

        if(tokenDTO == null){
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/comment/comment-list")
                            .queryParam("boardNo", String.valueOf(boardNo))
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getAmount())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }else if(tokenDTO != null){
            log.info("token is not null");
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/comment/comment-list")
                            .queryParam("boardNo", String.valueOf(boardNo))
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getAmount())
                            .build())
                    .cookie(tokenDTO.getAccessTokenHeader(), tokenDTO.getAccessTokenValue())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }

        log.info("boardComment responseVal : {}", responseVal);

        CommentListDTO dto;

        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        dto = om.readValue(responseVal, CommentListDTO.class);

        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }
}
