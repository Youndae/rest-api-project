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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardWebClient {

    private final WebClientConfig webClientConfig;

    private final TokenService tokenService;

    //계층형 게시판 list
    public HierarchicalBoardListDTO getHierarchicalBoardList(Criteria cri) throws JsonProcessingException {

        WebClient client = webClientConfig.useWebClient();

        String response = null;

        log.info("keyword : " + cri.getKeyword());
        log.info("searchType : " + cri.getSearchType());

        if (cri.getKeyword() == null || cri.getKeyword().equals("")) {
            log.info("keyword is null");

            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getBoardAmount())
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                                    Mono.error(
                                            new NotFoundException("not Found")
                                    )
                    )
                    .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                                    Mono.error(
                                            new NullPointerException()
                                    )
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("response : {}", response);


        } else if (cri.getKeyword() != null || !cri.getKeyword().equals("")) {
            log.info("keyword is not null");
            response = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-list")
                            .queryParam("pageNum", cri.getPageNum())
                            .queryParam("amount", cri.getBoardAmount())
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

        HierarchicalBoardListDTO dto;

        if (response != null) {
            dto = om.readValue(response, HierarchicalBoardListDTO.class);
        } else {
            JsonProcessingException Exception = null;
            throw Exception;
        }

        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    //계층형 게시판 상세페이지
    public HierarchicalBoardDetailDTO getHierarchicalBoardDetail(long boardNo, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        WebClient client = webClientConfig.useWebClient();

        String responseVal = null;

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            log.info("token is null");
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-detail/{boardNo}")
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
                    .block();
        }else if(tokenDTO != null){
            log.info("token is true");
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-detail/{boardNo}")
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
                    .block();
        }

        log.info("detail response : " + responseVal);

        ObjectMapper om = new ObjectMapper();

        HierarchicalBoardDetailDTO dto = om.readValue(responseVal, HierarchicalBoardDetailDTO.class);

        return dto;
    }

    // 계층형 게시판 글 작성
    public long hierarchicalBoardInsert(HttpServletRequest request, HttpServletResponse response) {
        WebClient client = webClientConfig.useWebClient();

        HierarchicalBoardDTO dto = HierarchicalBoardDTO.builder()
                .boardTitle(request.getParameter("boardTitle"))
                .boardContent(request.getParameter("boardContent"))
                .build();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        /**
         * 만약 모종의 이유(외부 공격 등)로 토큰이 둘다 존재하지 않는 경우가 발생한다면 요청을 보내지 않고 처리할 수 있도록 장치가 필요.
         **/
        if(tokenDTO == null){
            new AccessDeniedException("DeniedException");
        }

        return client.post()
                .uri(uriBuilder -> uriBuilder.path("/board/board-insert").build())
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
                .block();
    }

    // 계층형 게시판 수정 데이터 요청
    public HierarchicalBoardModifyDTO getModifyData(long boardNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response) throws JsonProcessingException {

        WebClient client = webClientConfig.useWebClient();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");


        String responseVal = client.get()
                .uri(uriBuilder -> uriBuilder.path("/board/board-modify/{boardNo}")
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
                .block();

        ObjectMapper om = new ObjectMapper();

        HierarchicalBoardModifyDTO dto = om.readValue(responseVal, HierarchicalBoardModifyDTO.class);

        return dto;

    }

    // 계층형 게시판 수정 요청
    public long modifyPatch(HttpServletRequest request, HttpServletResponse response){
        WebClient client = webClientConfig.useWebClient();
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                .boardNo(Long.parseLong(request.getParameter("boardNo")))
                .boardTitle(request.getParameter("boardTitle"))
                .boardContent(request.getParameter("boardContent"))
                .build();

        return client.patch()
                .uri(uriBuilder -> uriBuilder.path("/board/board-modify").build())
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
                .block();
    }

    // 계층형 게시판 삭제
    public int boardDelete(long boardNo, HttpServletRequest request, HttpServletResponse response){
        WebClient client = webClientConfig.useWebClient();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        try{
            client.delete()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-delete/{boardNo}")
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
        }
    }

    //계층형 게시판 답글 작성
    public long hierarchicalBoardReply(HttpServletRequest request, HttpServletResponse response){

        WebClient client = webClientConfig.useWebClient();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            new AccessDeniedException("Denied Exception");
        }

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                .boardNo(Long.parseLong(request.getParameter("boardNo")))
                .boardTitle(request.getParameter("boardTitle"))
                .boardContent(request.getParameter("boardContent"))
                .build();

        return client.post()
                .uri(uriBuilder -> uriBuilder.path("/board/board-reply").build())
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
                .block();
    }

}
