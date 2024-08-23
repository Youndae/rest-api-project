package com.board.boardapp.connection.webClient;

import com.board.boardapp.config.WebClientConfig;
import com.board.boardapp.config.properties.PathProperties;
import com.board.boardapp.domain.dto.*;
import com.board.boardapp.domain.dto.iBoard.in.ImageBoardInsertDTO;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.ExchangeService;
import com.board.boardapp.service.ObjectReadValueService;
import com.board.boardapp.service.UriComponentsService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardWebClient {

    private final WebClient webClient = new WebClientConfig().useWebClient();

    private final WebClient imageWebClient = new WebClientConfig().useImageWebClient();

    private final ObjectReadValueService readValueService;

    private final CookieService cookieService;

    private final UriComponentsService uriComponentsService;

    private final ExchangeService exchangeService;

    private static final String imagePath = PathProperties.IMAGE_BOARD_PATH;

    private static final String imagePath_variable = PathProperties.IMAGE_BOARD_PATH_VARIABLE;

    public PaginationListDTO<ImageBoardDTO> getList(Criteria cri, MultiValueMap<String, String> cookieMap, HttpServletResponse response) {
        UriComponentsBuilder ub = uriComponentsService.getListUri(imagePath, cri);

        String responseVal = webClient.get()
                                .uri(ub.toUriString())
                                .cookies(cookies -> cookies.addAll(cookieMap))
                                .exchangeToMono(res -> {
                                    exchangeService.checkExchangeResponse(res, response);

                                    return res.bodyToMono(String.class);
                                })
                                .block();

        ParameterizedTypeReference<PaginationListDTO<ImageBoardDTO>> typeReference =
                new ParameterizedTypeReference<PaginationListDTO<ImageBoardDTO>>() {};

        return readValueService.fromJsonWithPagination(typeReference, responseVal, cri);
    }

    public byte[] getImageDisplay(String imageName){

        /**
         * 메모리 초과 오류 발생.
         * 버퍼 사이즈를 추가한 메소드를 별도로 만들어서 처리.
         * 문제 해결 도움된 블로그 url
         * https://flyburi.com/617
         */
        return imageWebClient.get()
                            .uri(uriBuilder -> uriBuilder.path(imagePath + "display/{imageName}")
                                    .build(imageName))
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
                            .bodyToMono(byte[].class)
                            .block();
    }

    public BoardDetailAndModifyDTO<ImageBoardDetailDTO> getDetail(long imageNo
                                                , MultiValueMap<String, String> cookieMap
                                                , HttpServletResponse response) {

        String responseVal = webClient.get()
                                    .uri(uriBuilder -> uriBuilder.path(imagePath_variable).build(imageNo))
                                    .cookies(cookies -> cookies.addAll(cookieMap))
                                    .exchangeToMono(res -> {
                                        exchangeService.checkExchangeResponse(res, response);

                                        return res.bodyToMono(String.class);
                                    })
                                    .block();

        ParameterizedTypeReference<BoardDetailAndModifyDTO<ImageBoardDetailDTO>> typeReference =
                new ParameterizedTypeReference<BoardDetailAndModifyDTO<ImageBoardDetailDTO>>() {};

        return readValueService.fromJsonWithReference(typeReference, responseVal);
    }

    public Long insertBoard(ImageBoardInsertDTO dto
                                , List<MultipartFile> files
                                , MultiValueMap<String, String> cookieMap
                                , HttpServletResponse response){

        if(imageSizeCheck(files) == -2L)
            return -2L;

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        files.stream().forEach(file -> {
            mbBuilder.part("files", file.getResource());
        });

        mbBuilder.part("imageTitle", dto.getImageTitle());
        mbBuilder.part("imageContent", dto.getImageContent());

        return imageWebClient.post()
                            .uri(uriBuilder -> uriBuilder.path(imagePath).build())
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(res -> {
                                exchangeService.checkExchangeResponse(res, response);

                                return res.bodyToMono(Long.class);
                            })
                            .block();
    }

    // 이미지파일 사이즈 체크
    public long imageSizeCheck(List<MultipartFile> images) {
        for(MultipartFile image : images){
            if(image.getSize() >= 10 * 1024 * 1024){
                log.info("image size is larger than the limit size");
                return -2;
            }
        }

        return 1;
    }

    public BoardDetailAndModifyDTO<ImageBoardDTO> getPatchDetail(long imageNo
                                        , MultiValueMap<String, String> cookieMap
                                        , HttpServletResponse response){

        String responseVal = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/patch-detail/{imageNo}").build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

        ParameterizedTypeReference<BoardDetailAndModifyDTO<ImageBoardDTO>> typeReference =
                new ParameterizedTypeReference<BoardDetailAndModifyDTO<ImageBoardDTO>>() {};

        return readValueService.fromJsonWithReference(typeReference, responseVal);
    }

    public List<ImageDataDTO> getPatchImage(long imageNo
                                                    , MultiValueMap<String, String> cookieMap
                                                    , HttpServletResponse response) {

        String responseVal = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(imagePath + "/patch-detail/image/{imageNo}").build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();

       List<ImageDataDTO> dto = new ArrayList<>();

       return readValueService.fromJsonToList(dto, responseVal);
    }

    public Long patchBoard(long imageNo, ImageBoardInsertDTO dto
                                    , List<MultipartFile> files, List<String> deleteFiles
                                    , MultiValueMap<String, String> cookieMap, HttpServletResponse response){
        if(files != null && imageSizeCheck(files) == -2L)
            return -2L;

        MultipartBodyBuilder mbBuilder = new MultipartBodyBuilder();

        Optional.ofNullable(files)
                .orElseGet(Collections::emptyList)
                .forEach(file -> {
                    mbBuilder.part("files", file.getResource());
                });

        Optional.ofNullable(deleteFiles)
                .orElseGet(Collections::emptyList)
                .forEach(file -> {
                    mbBuilder.part("deleteFiles", file);
                });

        mbBuilder.part("imageTitle", dto.getImageTitle());
        mbBuilder.part("imageContent", dto.getImageContent());

        return imageWebClient.patch()
                            .uri(uriBuilder -> uriBuilder.path(imagePath_variable).build(imageNo))
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(mbBuilder.build()))
                            .cookies(cookies -> cookies.addAll(cookieMap))
                            .exchangeToMono(res -> {
                                exchangeService.checkExchangeResponse(res, response);

                                return res.bodyToMono(Long.class);
                            })
                            .block();
    }

    public String deleteBoard(long imageNo, MultiValueMap<String, String> cookieMap, HttpServletResponse response) {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(imagePath_variable).build(imageNo))
                .cookies(cookies -> cookies.addAll(cookieMap))
                .exchangeToMono(res -> {
                    exchangeService.checkExchangeResponse(res, response);

                    return res.bodyToMono(String.class);
                })
                .block();
    }
}
