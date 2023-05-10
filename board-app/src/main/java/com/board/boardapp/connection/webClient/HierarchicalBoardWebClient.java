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

    /**
     * return을 String으로 리턴할것인가
     * 아니면 이걸 다시 dto에 담아서 리턴을 할것인가
     * 아니면 Object로 parsing해서 리턴할것인가
     * <p>
     * 만약 String으로 return을 하는 경우 front에서 받아 처리할때 문제가 발생하지 않는가
     * Object 타입으로 리턴할 경우 이점이 무엇인가
     * dto 타입으로 리턴을 하게 되면 Collection 타입을 또 사용하게 될텐데 그 경우는 어떻게 할것이며
     * Page로 리턴이 되는 경우 그건 또 어떻게 처리를 할것인가
     *
     * 생각드는 의문점.
     * jquery에서 바로 API를 호출해서 처리해도 되는데
     * 굳이 서버에서 호출하도록 하는 이유는?
     * 이게 지금 검색에서 잘 나오질 않는데 생각되는 점.
     * jquery에서 api url로 요청을 보내게 되면
     * 바로 받아서 사용할 수 있으나 코드가 노출될 수 있다는 단점이 있다?
     * 아무래도 개발자도구에서 연결된 js 파일을 다 볼 수 있으니까.
     * 하지만 그렇다고 해도 어차피 request에는 인증방식(토큰이나 시큐리티 세션)정도 말고는
     * 넘어가는 데이터 정보는 중요한 데이터 정보가 없을것.
     * 근데 그건 어차피 단일 서버 방식에서도 동일한 조건이기 때문에 큰 문제는 없을것이라고 생각.
     *
     * 이걸 나눠서 사용할 때 서버에서 처리하도록 하는 이유는
     * 서버단에서 데이터 가공이 필요한 경우나, MVC 패턴을 유지하기 위한것이 아닌가 싶음.
     * 또 하나. API키가 꼭 있어야 하는 경우 이걸 클라이언트에서 보관하도록 할 수 없기 때문에 서버에서 API키를 보관하고 이 API키를 같이 보내야 하는 경우.
     * 아니면 이게 뭐 한 2020년 정도까지는 쓰고 그 뒤로는 안쓰는 방법이라거나?
     * MVC 패턴을 유지하기 위해 서버단에서 처리를 한다면 애초에 호출하는 위치가 페이지 생성 뒤가 아닌
     * 페이지 요청시에 바로 api를 호출해서 model에 담아 넘겨주는 방식이 유효하다고 보임.
     *
     * 하지만 이런 방식의 단점으로는 Application Server에도 DTO가 존재해야 하고.
     * 그럼 서버에서 수정이 발생할 때 모든 Application Server들의 DTO를 같이 수정해줘야 한다는 번거로움이 발생.
     * RESTAPI를 분리해서 사용하는 이유가 하나의 서버를 구축해놓고 그걸 호출해서 crud를 처리하도록 해
     * 다양한 구조의 서버(웹, 모바일 등)에서 처리할 수 있도록 하는게 목적인데
     * 애초에 설계를 잘 하면 되지! 라고 말하면 할말이 없지만 수정할 일은 존재할 수도 있는 것.
     * 그럼 그때마다 모든 서버의 DTO를 수정하는것이 맞는건지
     * 아니면 그렇더라도 DTO를 만들어서 처리하는것이 맞는건지
     * 아니면 MVC 패턴이 목적이 아니라 서버에서 꼭 연결을 해야하는 다른 이유가 있는것인지가 중요 포인트일듯.
     *
     */


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

        JwtDTO existsToken = tokenService.checkExistsToken(request, response);

        if(existsToken == null){
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
        }else if(existsToken != null){
            log.info("token is true");
            responseVal = client.get()
                    .uri(uriBuilder -> uriBuilder.path("/board/board-detail/{boardNo}")
                            .build(boardNo))
                    .cookie(existsToken.getAccessTokenHeader(), existsToken.getAccessTokenValue())
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

//        log.info("title : {} , content : {}", request.getParameter("boardTitle"), request.getParameter("boardContent"));

        HierarchicalBoardDTO dto = HierarchicalBoardDTO.builder()
                .boardTitle(request.getParameter("boardTitle"))
                .boardContent(request.getParameter("boardContent"))
                .build();

//        Cookie at = WebUtils.getCookie(request, JwtProperties.ACCESS_HEADER_STRING);

        Mono<HierarchicalBoardDTO> dto2 = Mono.just(
                HierarchicalBoardDTO.builder()
                        .build()
        );

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
