package com.board.boardapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class HierarchicalBoardController {

    @GetMapping("/boardList")
    public String hierarchicalBoardMain(){
        return "th/board/boardList";
    }

    @GetMapping("/boardDetail/{boardNo}")
    public String hierarchicalBoardDetail(Model model
                                            , @PathVariable long boardNo){
        model.addAttribute("boardNo", boardNo);

        return "th/board/boardDetail";
    }

    @GetMapping("/boardModify/{boardNo}")
    public String hierarchicalBoardModify(Model model
                                            , @PathVariable long boardNo){
        model.addAttribute("boardNo", boardNo);

        return "th/board/boardModify";
    }

    @GetMapping("/boardInsert")
    public String hierarchicalBoardInsert(){

        return "th/board/boardInsert";
    }

    @GetMapping("/boardReply/{boardNo}")
    public String hierarchicalBoardReply(Model model
                                            , @PathVariable long boardNo){

        model.addAttribute("boardNo", boardNo);

        return "th/board/boardReply";
    }
}
