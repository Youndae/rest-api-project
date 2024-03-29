package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.ImageBoardWebClient;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/")
    public String getList(Model model
                                , Criteria cri
                                , HttpServletRequest request
                                , HttpServletResponse response) {

        model.addAttribute(
                "data"
                , imageBoardWebClient.getList(cri, request, response)
        );

        return "th/imageBoard/imageBoardList";
    }

    @GetMapping("/{imageNo}")
    public String getDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        model.addAttribute(
                "data"
                , imageBoardWebClient.getDetail(imageNo, request, response)
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
    public long insertBoard(@RequestParam("imageTitle") String imageTitle
                                        , @RequestParam("imageContent") String imageContent
                                        , @RequestParam("files")List<MultipartFile> images
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        return imageBoardWebClient.insertBoard(imageTitle, imageContent, images, request, response);
    }

    @GetMapping("/patch/{imageNo}")
    public String getPatchDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "th/member/loginForm";

        model.addAttribute("data", imageBoardWebClient.getPatchDetail(imageNo, request, response));

        return "th/imageBoard/imageBoardModify";
    }

    @GetMapping("/patch/image")
    public ResponseEntity<List<ImageDataDTO>> getPatchImage(long imageNo, HttpServletRequest request, HttpServletResponse response) {

        return new ResponseEntity<>(imageBoardWebClient.getPatchImage(imageNo, request, response), HttpStatus.OK);
    }

    @PatchMapping("/{imageNo}")
    @ResponseBody
    public long patchBoard(@PathVariable("imageNo") long imageNo
                                    , @RequestParam("imageTitle") String imageTitle
                                    , @RequestParam("imageContent") String imageContent
                                    , @RequestParam(value = "files", required = false) List<MultipartFile> files
                                    , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        return imageBoardWebClient.patchBoard(imageNo, imageTitle, imageContent, files, deleteFiles, request, response);

    }

    @DeleteMapping("/{imageNo}")
    @ResponseBody
    public long deleteBoard(@PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        return imageBoardWebClient.deleteBoard(imageNo, request, response);

    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getImageDisplay(@PathVariable String imageName){

        return new ResponseEntity<>(imageBoardWebClient.getImageDisplay(imageName), HttpStatus.OK);
    }
}
