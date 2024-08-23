package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.ImageBoardWebClient;
import com.board.boardapp.domain.dto.Criteria;
import com.board.boardapp.domain.dto.ImageDataDTO;
import com.board.boardapp.domain.dto.LoginDTO;
import com.board.boardapp.domain.dto.UserStatusDTO;
import com.board.boardapp.domain.dto.iBoard.in.ImageBoardInsertDTO;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/imageBoard")
@Slf4j
@RequiredArgsConstructor
public class ImageBoardController {

    private final ImageBoardWebClient imageBoardWebClient;

    private final TokenService tokenService;

    private final CookieService cookieService;

    @GetMapping("/")
    public String getList(Model model
                                , Criteria cri
                                , HttpServletRequest request
                                , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        model.addAttribute(
                "data"
                , imageBoardWebClient.getList(cri, cookieMap, response)
        );

        return "th/imageBoard/imageBoardList";
    }

    @GetMapping("/{imageNo}")
    public String getDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        model.addAttribute(
                "data"
                , imageBoardWebClient.getDetail(imageNo, cookieMap, response)
        );

        return "th/imageBoard/imageBoardDetail";
    }

    @GetMapping("/post")
    public String getInsertPage(HttpServletRequest request, Model model){

        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "redirect:/member/login";

        LoginDTO dto = new LoginDTO(new UserStatusDTO(true));
        model.addAttribute("data", dto);

        return "th/imageBoard/imageBoardInsert";
    }

    @PostMapping("/")
    @ResponseBody
    public Long insertBoard(@ModelAttribute ImageBoardInsertDTO dto
                                        , @RequestParam("files")List<MultipartFile> images
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return imageBoardWebClient.insertBoard(dto, images, cookieMap, response);
    }

    @GetMapping("/patch/{imageNo}")
    public String getPatchDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "th/member/loginForm";

        model.addAttribute("data", imageBoardWebClient.getPatchDetail(imageNo, cookieMap, response));

        return "th/imageBoard/imageBoardModify";
    }

    @GetMapping("/patch/image")
    public ResponseEntity<List<ImageDataDTO>> getPatchImage(long imageNo, HttpServletRequest request, HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return new ResponseEntity<>(imageBoardWebClient.getPatchImage(imageNo, cookieMap, response), HttpStatus.OK);
    }

    /**
     * DTO로 받도록 처리?
     */
    @PatchMapping("/{imageNo}")
    @ResponseBody
    public long patchBoard(@PathVariable("imageNo") long imageNo
                                    , @ModelAttribute ImageBoardInsertDTO dto
                                    , @RequestParam(value = "files", required = false) List<MultipartFile> files
                                    , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return imageBoardWebClient.patchBoard(imageNo, dto, files, deleteFiles, cookieMap, response);

    }

    @DeleteMapping("/{imageNo}")
    @ResponseBody
    public String deleteBoard(@PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return imageBoardWebClient.deleteBoard(imageNo, cookieMap, response);

    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getImageDisplay(@PathVariable String imageName){

        return new ResponseEntity<>(imageBoardWebClient.getImageDisplay(imageName), HttpStatus.OK);
    }
}
