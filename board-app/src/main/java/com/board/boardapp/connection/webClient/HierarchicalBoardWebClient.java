package com.board.boardapp.connection.webClient;

import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final ObjectReadValueService readValueService;

    private final CookieService cookieService;

    private final ExchangeService exchangeService;

    private static final String boardPath = PathProperties.BOARD_PATH;

    private static final String boardPath_variable = PathProperties.BOARD_PATH_VARIABLE;

    //계층형 게시판 list
    public HierarchicalBoardListDTO getList(Criteria cri, HttpServletRequest request, HttpServletResponse response) {
        UriComponents ub = UriComponentsBuilder.newInstance()
                                                .path(boardPath)
                                                .queryParam("pageNum", cri.getPageNum())
                                                .build();

        if(cri.getKeyword() != null)
            ub = UriComponentsBuilder.newInstance()
                                    .path(boardPath)
                                    .queryParam("pageNum", cri.getPageNum())
                                    .queryParam("keyword", cri.getKeyword())
                                    .queryParam("searchType", cri.getSearchType())
                                    .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String result = webClient.get()
                                    .uri(ub.toUriString())
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);
                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

        HierarchicalBoardListDTO dto = new HierarchicalBoardListDTO();
        dto = readValueService.setReadValue(dto, result);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    //계층형 게시판 상세페이지
    public BoardDetailAndModifyDTO<HierarchicalBoardDTO> getDetail(long boardNo, HttpServletRequest request, HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String responseVal = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    // 계층형 게시판 글 작성
    public Long postBoard(HttpServletRequest request, HttpServletResponse response) {
        HierarchicalBoardDTO dto = HierarchicalBoardDTO.builder()
                                                        .boardTitle(request.getParameter("boardTitle"))
                                                        .boardContent(request.getParameter("boardContent"))
                                                        .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

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
                                                                            , HttpServletRequest request
                                                                            , HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String responseVal = webClient.get()
                                        .uri(uriBuilder -> uriBuilder.path(boardPath + "patch-detail/{boardNo}")
                                                .build(boardNo))
                                        .cookies(cookies -> cookies.addAll(cookieMap))
                                        .exchangeToMono(res -> {
                                            exchangeService.checkExchangeResponse(res, response);

                                            return res.bodyToMono(String.class);
                                        })
                                        .block();

        BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    // 계층형 게시판 수정 요청
    public Long patchBoard(long boardNo, HttpServletRequest request, HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                    .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                                    .boardTitle(request.getParameter("boardTitle"))
                                                    .boardContent(request.getParameter("boardContent"))
                                                    .build();*/

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                .boardTitle(request.getParameter("boardTitle"))
                                                .boardContent(request.getParameter("boardContent"))
                                                .build();

        Long responseValue = webClient.patch()
                                    .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                                    .accept()
                                    .body(Mono.just(dto), HierarchicalBoardModifyDTO.class)
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(Long.class);
                                    })
                                    .block();

        if(responseValue == 0L)
            throw new CustomNotFoundException(ErrorCode.DATA_NOT_FOUND);
        else
            return responseValue;
    }

    // 계층형 게시판 삭제
    public Long deleteBoard(long boardNo, HttpServletRequest request, HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(boardPath_variable).build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();
    }

    public BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> getReplyDetail(
                                                                HttpServletRequest request
                                                                , HttpServletResponse response
                                                                , long boardNo){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String result = webClient.get()
                                    .uri(uriBuilder -> uriBuilder.path(boardPath + "reply/{boardNo}").build(boardNo))
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

        BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, result);

        return dto;
    }

    //계층형 게시판 답글 작성
    public Long postReply(HttpServletRequest request, HttpServletResponse response){
        HierarchicalBoardReplyDTO dto = HierarchicalBoardReplyDTO.builder()
                                            .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                            .boardTitle(request.getParameter("boardTitle"))
                                            .boardContent(request.getParameter("boardContent"))
                                            .boardGroupNo(Long.parseLong(request.getParameter("boardGroupNo")))
                                            .boardIndent(Integer.parseInt(request.getParameter("boardIndent")))
                                            .boardUpperNo(request.getParameter("boardUpperNo"))
                                            .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

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
