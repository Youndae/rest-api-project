package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.domain.dto.*;
import com.board.boardapp.domain.dto.comment.in.CommentInsertDTO;
import com.board.boardapp.domain.dto.comment.in.CommentReplyInsertDTO;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentBoardWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final ObjectReadValueService readValueService;

    private final ExchangeService exchangeService;

    private static final String commentPath = PathProperties.COMMENT_PATH;

    public PaginationListDTO<CommentDTO> getList(String boardType
                                , long boardNo
                                , Criteria cri
                                , MultiValueMap<String, String> cookieMap
                                , HttpServletResponse response) {
        String boardQueryParamName;

        if(boardType.equals("board"))
            boardQueryParamName = "boardNo";
        else if(boardType.equals("image"))
            boardQueryParamName = "imageNo";
        else
            throw new NullPointerException("comment Board Type NullPointerException");


        String responseVal = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(commentPath)
                        .queryParam(boardQueryParamName, String.valueOf(boardNo))
                        .queryParam("pageNum", cri.getPageNum())
                        .build())
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(resp -> {
                    exchangeService.checkExchangeResponse(resp, response);

                    return resp.bodyToMono(String.class);
                })
                .block();

        ParameterizedTypeReference<PaginationListDTO<CommentDTO>> typeReference =
                new ParameterizedTypeReference<PaginationListDTO<CommentDTO>>() {};

        return readValueService.fromJsonWithPagination(typeReference, responseVal, cri);
    }

    public String commentInsert(CommentInsertDTO dto
                                , MultiValueMap<String, String> cookieMap
                                , HttpServletResponse response){

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(commentPath).build())
                .body(Mono.just(dto), CommentInsertDTO.class)
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(resp -> {
                    exchangeService.checkExchangeResponse(resp, response);
                    return resp.bodyToMono(String.class);
                })
                .block();
    }

    public String commentReplyInsert(CommentReplyInsertDTO dto
                                    , MultiValueMap<String, String> cookieMap
                                    , HttpServletResponse response){


        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(commentPath + "reply").build())
                .body(Mono.just(dto), CommentReplyInsertDTO.class)
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(resp -> {
                    exchangeService.checkExchangeResponse(resp, response);
                    return resp.bodyToMono(String.class);
                })
                .block();
    }

    public String commentDelete(long commentNo, MultiValueMap<String, String> cookieMap, HttpServletResponse response){


        return webClient.delete()
                            .uri(uriBuilder -> uriBuilder.path(commentPath + "{commentNo}").build(commentNo))
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(resp -> {
                                exchangeService.checkExchangeResponse(resp, response);

                                return resp.bodyToMono(String.class);
                            })
                            .block();
    }
}
