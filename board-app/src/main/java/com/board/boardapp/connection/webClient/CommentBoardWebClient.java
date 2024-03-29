package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentBoardWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final ObjectReadValueService readValueService;

    private final CookieService cookieService;

    private final ExchangeService exchangeService;

    private static final String commentPath = PathProperties.COMMENT_PATH;

    public CommentListDTO getList(String boardType
                                , long boardNo
                                , Criteria cri
                                , HttpServletRequest request
                                , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String boardQueryParamName;

        if(boardType.equals("board")){
            boardQueryParamName = "boardNo";
        }else if(boardType.equals("image")) {
            boardQueryParamName = "imageNo";
        } else {
            throw new NullPointerException("comment Board Type NullPointerException");
        }

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

        CommentListDTO dto = new CommentListDTO();
        dto = readValueService.setReadValue(dto, responseVal);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }
/*
    public CommentListDTO getBoardComment(long boardNo
                                            , Criteria cri
                                            , HttpServletRequest request
                                            , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String responseVal = webClient.get()
                                    .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-list")
                                            .queryParam("boardNo", String.valueOf(boardNo))
                                            .queryParam("pageNum", cri.getPageNum())
                                            .build())
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(resp -> {
                                        exchangeService.checkExchangeResponse(resp, response);
                                        return resp.bodyToMono(String.class);
                                    })
                                    .block();

        CommentListDTO dto = new CommentListDTO();
        dto = readValueService.setReadValue(dto, responseVal);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    public CommentListDTO getImageComment(long imageNo
                                            , Criteria cri
                                            , HttpServletRequest request
                                            , HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String responseVal = webClient.get()
                                    .uri(uriBuilder -> uriBuilder.path(commentPath + "/comment-list")
                                            .queryParam("imageNo", String.valueOf(imageNo))
                                            .queryParam("pageNum", cri.getPageNum())
                                            .build())
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(resp -> {
                                        exchangeService.checkExchangeResponse(resp, response);
                                        return resp.bodyToMono(String.class);
                                    })
                                    .block();

        CommentListDTO dto = new CommentListDTO();
        dto = readValueService.setReadValue(dto, responseVal);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }*/

    public Long commentInsert(Map<String, Object> commentData
                                , HttpServletRequest request
                                , HttpServletResponse response){
        Long imageNo = commentData.get("imageNo") == null ? null : Long.parseLong(commentData.get("imageNo").toString());
        Long boardNo = commentData.get("boardNo") == null ? null : Long.parseLong(commentData.get("boardNo").toString());

        CommentDTO dto = CommentDTO.builder()
                                    .commentContent(commentData.get("commentContent").toString())
                                    .imageNo(imageNo)
                                    .boardNo(boardNo)
                                    .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        Long result = webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(commentPath).build())
                            .body(Mono.just(dto), CommentDTO.class)
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(resp -> {
                                exchangeService.checkExchangeResponse(resp, response);
                                return resp.bodyToMono(Long.class);
                            })
                            .block();

        return result;
    }

    public Long commentReplyInsert(Map<String, Object> commentData
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

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

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        Long result = webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(commentPath + "reply").build())
                            .body(Mono.just(dto), CommentDTO.class)
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(resp -> {
                                exchangeService.checkExchangeResponse(resp, response);
                                return resp.bodyToMono(Long.class);
                            })
                            .block();

        return result;
    }

    public Long commentDelete(long commentNo, HttpServletRequest request, HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        Long result = webClient.delete()
                            .uri(uriBuilder -> uriBuilder.path(commentPath + "{commentNo}").build(commentNo))
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(resp -> {
                                exchangeService.checkExchangeResponse(resp, response);

                                return resp.bodyToMono(Long.class);
                            })
                            .block();

        return result;
    }
}
