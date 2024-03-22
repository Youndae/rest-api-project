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
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardWebClient {

    private final TokenService tokenService;

    private final WebClient clientConfig = new WebClientConfig().useWebClient();

    private final ObjectReadValueService readValueService;

    private final CookieService cookieService;

    private final ExchangeService exchangeService;

    private static final String boardPath = PathProperties.BOARD_PATH;

    //계층형 게시판 list
    public HierarchicalBoardListDTO getHierarchicalBoardList(Criteria cri, HttpServletRequest request, HttpServletResponse response) {
        String path = boardPath + "/board-list";
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

        String result = clientConfig.get()
                                .uri(ub.toUriString())
                                .cookies(cookies -> cookies.addAll(cookieMap))
                                .exchangeToMono(res -> {
                                    exchangeService.checkExchangeResponse(res, response);
                                    return res.bodyToMono(String.class);
                                })
                                .block();

        /*String result = clientConfig.get()
                                        .uri(ub.toUriString())
                                        .cookies(cookies -> cookies.addAll(cookieMap))
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

        System.out.println("list response : " + result);

        HierarchicalBoardListDTO dto = new HierarchicalBoardListDTO();
        dto = readValueService.setReadValue(dto, result);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));
//        dto.setLoggedIn(true);

        System.out.println("response dto : " + dto);

        return dto;
    }

    //계층형 게시판 상세페이지
    public BoardDetailAndModifyDTO<HierarchicalBoardDTO> getHierarchicalBoardDetail(long boardNo, HttpServletRequest request, HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);


        String responseVal = clientConfig.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-detail/{boardNo}").build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

        /*String responseVal = clientConfig.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-detail/{boardNo}")
                        .build(boardNo))
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

//        HierarchicalBoardDTO dto = new HierarchicalBoardDTO();
        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = new BoardDetailAndModifyDTO<>();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    // 계층형 게시판 글 작성
    public Long hierarchicalBoardInsert(HttpServletRequest request, HttpServletResponse response) {
        HierarchicalBoardDTO dto = HierarchicalBoardDTO.builder()
                                                        .boardTitle(request.getParameter("boardTitle"))
                                                        .boardContent(request.getParameter("boardContent"))
                                                        .build();

//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        /**
         * 만약 모종의 이유(외부 공격 등)로 토큰이 둘다 존재하지 않는 경우가 발생한다면 요청을 보내지 않고 처리할 수 있도록 장치가 필요.
         **/
//        if(tokenDTO == null)
//            new AccessDeniedException("DeniedException");

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);


        /*return clientConfig.post()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-insert").build())
                .accept()
                .body(Mono.just(dto), HierarchicalBoardDTO.class)
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

        return clientConfig.post()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-insert").build())
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
    public BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> getModifyData(long boardNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response) {
//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);


        /*String responseVal = clientConfig.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-modify/{boardNo}")
                        .build(boardNo))
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

        String responseVal = clientConfig.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-modify/{boardNo}")
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
    public Long modifyPatch(HttpServletRequest request, HttpServletResponse response){
//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                    .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                                    .boardTitle(request.getParameter("boardTitle"))
                                                    .boardContent(request.getParameter("boardContent"))
                                                    .build();

        /*return clientConfig.patch()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-modify").build())
                .accept()
                .body(Mono.just(dto), HierarchicalBoardModifyDTO.class)
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

        return clientConfig.patch()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-modify").build())
                .accept()
                .body(Mono.just(dto), HierarchicalBoardModifyDTO.class)
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();
    }

    // 계층형 게시판 삭제
    public Long boardDelete(long boardNo, HttpServletRequest request, HttpServletResponse response){
//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        Long result = clientConfig.delete()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-delete/{boardNo}")
                        .build(boardNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(Long.class);
                })
                .block();

        return result;

        /*try{
            clientConfig.delete()
                    .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-delete/{boardNo}")
                            .build(boardNo))
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

            return 1;
        }catch (Exception e){
            return 0;
        }*/
    }

    public BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> getHierarchicalBoardReplyInfo(
                                                                HttpServletRequest request
                                                                , HttpServletResponse response
                                                                , long boardNo){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        String result = clientConfig.get()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-reply-info/{boardNo}").build(boardNo))
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
    public Long hierarchicalBoardReply(HttpServletRequest request, HttpServletResponse response){
//        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

//        if(tokenDTO == null)
//            new AccessDeniedException("Denied Exception");

        /*HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                                .boardTitle(request.getParameter("boardTitle"))
                                                .boardContent(request.getParameter("boardContent"))
                                                .build();*/


        HierarchicalBoardReplyDTO dto = HierarchicalBoardReplyDTO.builder()
                .boardNo(Long.parseLong(request.getParameter("boardNo")))
                .boardTitle(request.getParameter("boardTitle"))
                .boardContent(request.getParameter("boardContent"))
                .boardGroupNo(Long.parseLong(request.getParameter("boardGroupNo")))
                .boardIndent(Integer.parseInt(request.getParameter("boardIndent")))
                .boardUpperNo(request.getParameter("boardUpperNo"))
                .build();

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        /*return clientConfig.post()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-reply").build())
                .accept()
                .body(Mono.just(dto), HierarchicalBoardModifyDTO.class)
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

        return clientConfig.post()
                .uri(uriBuilder -> uriBuilder.path(boardPath + "/board-reply").build())
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
