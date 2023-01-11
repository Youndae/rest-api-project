package com.board.boardapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/imageBoard")
public class ImageBoardController {

    @GetMapping("/imageBoardList")
    public String imageBoardMain(){

        return "th/imageBoard/imageBoardList";
    }

    @GetMapping("/imageBoardDetail/{imageNo}")
    public String imageBoardDetail(Model model
                                    , @PathVariable long imageNo){
        model.addAttribute("imageNo", imageNo);

        return "th/imageBoard/imageBoardDetail";
    }

    @GetMapping("/imageBoardInsert")
    public String imageBoardInsert(){
        return "th/imageBoard/imageBoardInsert";
    }

    @GetMapping("/imageBoardModify/{imageNo}")
    public String imageBoardModify(Model model
                                    , @PathVariable long imageNo){
        model.addAttribute("imageNo", imageNo);

        return "th/imageBoard/imageBoardModify";
    }
}
