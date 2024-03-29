package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.ImageBoardWebClient;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @GetMapping("/imageBoardList")
    public String imageBoardMain(Model model
                                , Criteria cri
                                , HttpServletRequest request
                                , HttpServletResponse response) {

        model.addAttribute(
                "data"
                , imageBoardWebClient.getImageBoardList(cri, request, response)
        );

        return "th/imageBoard/imageBoardList";
    }

    @GetMapping("/imageBoardDetail/{imageNo}")
    public String imageBoardDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        model.addAttribute(
                "data"
                , imageBoardWebClient.getImageDetail(imageNo, request, response)
        );

        return "th/imageBoard/imageBoardDetail";
    }

    @GetMapping("/imageBoardInsert")
    public String imageBoardInsert(HttpServletRequest request, Model model){

        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "redirect:/member/loginForm";

        LoginDTO dto = new LoginDTO(new UserStatusDTO(true));
        model.addAttribute("data", dto);

        return "th/imageBoard/imageBoardInsert";
    }

    @PostMapping("/imageBoardInsert")
    @ResponseBody
    public long imageBoardInsertProc(@RequestParam("imageTitle") String imageTitle
                                        , @RequestParam("imageContent") String imageContent
                                        , @RequestParam("files")List<MultipartFile> images
                                        , HttpServletRequest request
                                        , HttpServletResponse response){

        return imageBoardWebClient.imageBoardInsert(imageTitle, imageContent, images, request, response);
    }

    @GetMapping("/imageBoardModify/{imageNo}")
    public String imageBoardModify(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "th/member/loginForm";

        model.addAttribute("data", imageBoardWebClient.getModifyData(imageNo, request, response));

        return "th/imageBoard/imageBoardModify";
    }

    @GetMapping("/modifyImageAttach")
    public ResponseEntity<List<ImageDataDTO>> modifyImageAttach(long imageNo, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        return new ResponseEntity<>(imageBoardWebClient.getModifyImageList(imageNo, request, response), HttpStatus.OK);
    }

    @PatchMapping("/imageBoardModify")
    @ResponseBody
    public long imageBoardModifyProc(@RequestParam("imageNo") long imageNo
                                    , @RequestParam("imageTitle") String imageTitle
                                    , @RequestParam("imageContent") String imageContent
                                    , @RequestParam(value = "files", required = false) List<MultipartFile> files
                                    , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        return imageBoardWebClient.imageBoardModify(imageNo, imageTitle, imageContent, files, deleteFiles, request, response);

    }

    @DeleteMapping("/imageBoardDelete/{imageNo}")
    @ResponseBody
    public long imageBoardDelete(@PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) {

        return imageBoardWebClient.imageBoardDelete(imageNo, request, response);

    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getImageDisplay(@PathVariable String imageName){

        return new ResponseEntity<>(imageBoardWebClient.getImageDisplay(imageName), HttpStatus.OK);
    }
}
