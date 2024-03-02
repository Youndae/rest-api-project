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

    private static final String boardPath = PathProperties.BOARD_PATH;

    //계층형 게시판 list
    public HierarchicalBoardListDTO getHierarchicalBoardList(Criteria cri) {
        String path = boardPath + "/board-list";
        UriComponents ub = UriComponentsBuilder.newInstance()
                                                .path(path)
                                                .queryParam("pageNum", cri.getPageNum())
                                                .queryParam("amount", cri.getBoardAmount())
                                                .build();

        if(cri.getKeyword() != null)
            ub = UriComponentsBuilder.newInstance()
                    .path(path)
                    .queryParam("pageNum", cri.getPageNum())
                    .queryParam("amount", cri.getBoardAmount())
                    .queryParam("keyword", cri.getKeyword())
                    .queryParam("searchType", cri.getSearchType())
                    .build();

        String response = clientConfig.get()
                                        .uri(ub.toUriString())
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

        HierarchicalBoardListDTO dto = new HierarchicalBoardListDTO();
        dto = readValueService.setReadValue(dto, response);
        dto.setPageDTO(new PageDTO(cri, dto.getTotalPages()));

        return dto;
    }

    //계층형 게시판 상세페이지
    public HierarchicalBoardDTO getHierarchicalBoardDetail(long boardNo) {
        String responseVal = clientConfig.get()
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
                .block();

        HierarchicalBoardDTO dto = new HierarchicalBoardDTO();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    // 계층형 게시판 글 작성
    public Long hierarchicalBoardInsert(HttpServletRequest request, HttpServletResponse response) {
        HierarchicalBoardDTO dto = HierarchicalBoardDTO.builder()
                                                        .boardTitle(request.getParameter("boardTitle"))
                                                        .boardContent(request.getParameter("boardContent"))
                                                        .build();

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        /**
         * 만약 모종의 이유(외부 공격 등)로 토큰이 둘다 존재하지 않는 경우가 발생한다면 요청을 보내지 않고 처리할 수 있도록 장치가 필요.
         **/
        if(tokenDTO == null)
            new AccessDeniedException("DeniedException");


        return clientConfig.post()
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
                .block();
    }

    // 계층형 게시판 수정 데이터 요청
    public HierarchicalBoardModifyDTO getModifyData(long boardNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response) throws JsonProcessingException {
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        String responseVal = clientConfig.get()
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
                .block();

        HierarchicalBoardModifyDTO dto = new HierarchicalBoardModifyDTO();
        dto = readValueService.setReadValue(dto, responseVal);

        return dto;
    }

    // 계층형 게시판 수정 요청
    public Long modifyPatch(HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                    .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                                    .boardTitle(request.getParameter("boardTitle"))
                                                    .boardContent(request.getParameter("boardContent"))
                                                    .build();

        return clientConfig.patch()
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
                .block();
    }

    // 계층형 게시판 삭제
    public int boardDelete(long boardNo, HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        try{
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
        }
    }

    //계층형 게시판 답글 작성
    public Long hierarchicalBoardReply(HttpServletRequest request, HttpServletResponse response){
        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            new AccessDeniedException("Denied Exception");

        HierarchicalBoardModifyDTO dto = HierarchicalBoardModifyDTO.builder()
                                                .boardNo(Long.parseLong(request.getParameter("boardNo")))
                                                .boardTitle(request.getParameter("boardTitle"))
                                                .boardContent(request.getParameter("boardContent"))
                                                .build();

        return clientConfig.post()
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
                .block();
    }
}
