package com.board.boardapp.connection.webClient;

import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.domain.dto.*;
import com.board.boardapp.domain.dto.hBoard.in.HierarchicalBoardInsertDTO;
import com.board.boardapp.domain.dto.hBoard.in.HierarchicalBoardReplyInsertDTO;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import com.board.boardapp.service.UriComponentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final ObjectReadValueService readValueService;

    private final UriComponentsService uriComponentsService;

    private final ExchangeService exchangeService;

    private static final String boardPath = PathProperties.BOARD_PATH;

    private static final String boardPath_variable = PathProperties.BOARD_PATH_VARIABLE;

    //계층형 게시판 list
    public PaginationListDTO<HierarchicalBoardDTO> getList(Criteria cri, MultiValueMap<String, String> cookieMap, HttpServletResponse response) {
        UriComponentsBuilder ub = uriComponentsService.getListUri(boardPath, cri);

        String responseVal = webClient.get()
                                    .uri(ub.toUriString())
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);
                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

        log.info("hboard list responseVal : {}", responseVal);

        ParameterizedTypeReference<PaginationListDTO<HierarchicalBoardDTO>> typeReference =
                new ParameterizedTypeReference<PaginationListDTO<HierarchicalBoardDTO>>() {};

        PaginationListDTO<HierarchicalBoardDTO> dto = readValueService.fromJsonWithPagination(typeReference, responseVal, cri);

        log.info("hboard list dto : {}", dto);

        return dto;
    }

    //계층형 게시판 상세페이지
    public BoardDetailAndModifyDTO<HierarchicalBoardDTO> getDetail(long boardNo, MultiValueMap<String, String> cookieMap, HttpServletResponse response) {

        String responseVal = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();


        ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardDTO>> typeReference = new ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardDTO>>() {};

        return readValueService.fromJsonWithReference(typeReference, responseVal);
    }

    // 계층형 게시판 글 작성
    public Long postBoard(HierarchicalBoardInsertDTO dto, MultiValueMap<String, String> cookieMap, HttpServletResponse response) {

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(boardPath).build())
                        .accept()
                        .body(Mono.just(dto), HierarchicalBoardDTO.class)
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(Long.class);
                        })
                        .block();
    }

    // 계층형 게시판 수정 데이터 요청
    public BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> getPatchDetail(long boardNo
                                                                            , MultiValueMap<String, String> cookieMap
                                                                            , HttpServletResponse response) {

        String responseVal = webClient.get()
                                        .uri(uriBuilder -> uriBuilder.path(boardPath + "patch-detail/{boardNo}")
                                                .build(boardNo))
                                        .cookies(cookies -> cookies.addAll(cookieMap))
                                        .exchangeToMono(res -> {
                                            exchangeService.checkExchangeResponse(res, response);

                                            return res.bodyToMono(String.class);
                                        })
                                        .block();

        ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO>> typeReference =
                new ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO>>() {};

        return readValueService.fromJsonWithReference(typeReference, responseVal);
    }

    // 계층형 게시판 수정 요청
    public Long patchBoard(long boardNo, HierarchicalBoardInsertDTO dto, MultiValueMap<String, String> cookieMap, HttpServletResponse response){

        Long responseVal = webClient.patch()
                                    .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                                    .accept()
                                    .body(Mono.just(dto), HierarchicalBoardModifyDTO.class)
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(Long.class);
                                    })
                                    .block();

        if(responseVal == 0L)
            throw new CustomNotFoundException(ErrorCode.DATA_NOT_FOUND);
        else
            return responseVal;
    }

    // 계층형 게시판 삭제
    public String deleteBoard(long boardNo, MultiValueMap<String, String> cookieMap, HttpServletResponse response){

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();
    }

    public BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> getReplyDetail(
                                                                MultiValueMap<String, String> cookieMap
                                                                , HttpServletResponse response
                                                                , long boardNo){

        String responseVal = webClient.get()
                                    .uri(uriBuilder -> uriBuilder.path(boardPath + "reply/{boardNo}").build(boardNo))
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

        ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO>> typeReference = new ParameterizedTypeReference<BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO>>() {};

        return readValueService.fromJsonWithReference(typeReference, responseVal);
    }

    //계층형 게시판 답글 작성
    public Long postReply(HierarchicalBoardReplyInsertDTO dto, MultiValueMap<String, String> cookieMap, HttpServletResponse response){

        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(boardPath + "reply").build())
                        .accept()
                        .body(Mono.just(dto), HierarchicalBoardReplyDTO.class)
                        .cookies(cookies -> cookies.addAll(cookieMap))
                        .exchangeToMono(res -> {
                            exchangeService.checkExchangeResponse(res, response);

                            return res.bodyToMono(Long.class);
                        })
                        .block();
    }
}
