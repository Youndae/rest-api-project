package com.board.boardapp.controller;

import com.board.boardapp.connection.RestCallWebClient;
import com.board.boardapp.dto.HierarchicalBoardDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/board")
public class HierarchicalBoardController {

    @Autowired
    private RestCallWebClient restCallWebClient;

    @GetMapping("/boardList")
    public String hierarchicalBoardMain(){

        return "th/board/boardList";
    }

    @GetMapping("/boardListData")
    public ResponseEntity<List<HierarchicalBoardDTO>> hierarchicalBoardData() throws JsonProcessingException {

        return new ResponseEntity<>(restCallWebClient.getHierarchicalBoardList(), HttpStatus.OK);
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
