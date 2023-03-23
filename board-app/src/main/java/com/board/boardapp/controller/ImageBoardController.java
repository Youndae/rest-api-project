package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.ImageBoardWebClient;
import com.board.boardapp.dto.Criteria;
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

    @GetMapping("/imageBoardList")
    public String imageBoardMain(Model model, Criteria cri) throws JsonProcessingException {
        /**
         * @Data
         * 1. imageTitle
         * 2. imageData - imageName(step 1 thumbnail)
         * 3. imageNo
         */
        model.addAttribute("imageList", imageBoardWebClient.getImageBoardList(cri));

        return "th/imageBoard/imageBoardList";
    }

    @GetMapping("/imageBoardDetail/{imageNo}")
    public String imageBoardDetail(Model model
                                    , @PathVariable long imageNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response) throws JsonProcessingException {
        /**
         * @Data
         * 1. imageTitle
         * 2. uid(login user)
         * 3. userid(writer)
         * 4. imageDate
         * 5. imageNo
         * 6. imageContent
         * 7. imageDataList
         *
         * or
         *
         * 1. imageTitle
         * 2. uid(login user)
         * 3. userid(writer)
         * 4. imageDate
         * 5. imageNo
         * 6. imageContent
         *  +
         * getJSON('/imageBoard/imageList')
         */
        model.addAttribute("image", imageBoardWebClient.getImageDetail(imageNo, request, response));

        return "th/imageBoard/imageBoardDetail";
    }

    @GetMapping("/imageBoardInsert")
    public String imageBoardInsert(){
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
                                    , @PathVariable long imageNo){
        model.addAttribute("imageNo", imageNo);

        return "th/imageBoard/imageBoardModify";
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getImageDisplay(String imageName, HttpServletRequest request, HttpServletResponse response){
        log.info("imageName : {}", imageName);

        return new ResponseEntity<>(imageBoardWebClient.getImageDisplay(imageName, request, response), HttpStatus.OK);
    }
}
