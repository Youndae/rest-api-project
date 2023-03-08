package com.board.boardapp.controller;

import com.board.boardapp.connection.RestCallWebClient;
import com.board.boardapp.dto.Criteria;
import com.board.boardapp.dto.HierarchicalBoardDTO;
import com.board.boardapp.dto.HierarchicalBoardListDTO;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardController {


    private final RestCallWebClient restCallWebClient;

    private final TokenService tokenService;

    @GetMapping("/boardList")
    public String hierarchicalBoardMain(Model model
                                            , Criteria cri) throws JsonProcessingException {

        model.addAttribute("boardList", restCallWebClient.getHierarchicalBoardList(cri));

        log.info("boardList Controller");

        return "th/board/boardList";
    }

    @GetMapping("/boardDetail/{boardNo}")
    public String hierarchicalBoardDetail(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response) throws JsonProcessingException {

        System.out.println("boardNo : " + boardNo);

//        System.out.println(principal.getName());

        model.addAttribute("board", restCallWebClient.getHierarchicalBoardDetail(boardNo, request, response));

        log.info("boardDetail");

        return "th/board/boardDetail";
    }

    @GetMapping("/boardModify/{boardNo}")
    public String hierarchicalBoardModify(Model model
                                            , @PathVariable long boardNo){

        model.addAttribute("boardNo", boardNo);

        return "th/board/boardModify";
    }

    @GetMapping("/boardInsert")
    public String hierarchicalBoardInsert(HttpServletRequest request){

        String existsToken = tokenService.checkExistsToken(request);

        if(existsToken == "T" || existsToken == "F")
            return "th/board/boardInsert";
        else
            return "th/member/loginForm";

    }

    @PostMapping("/boardInsert")
    public String hierarchicalBoardInsertProc(HttpServletRequest request){


        long response = restCallWebClient.hierarchicalBoardInsert(request);

        log.info("controller insertProc response : {}", response);

        return "redirect:/board/boardDetail/" + response;
    }

    @GetMapping("/boardReply/{boardNo}")
    public String hierarchicalBoardReply(Model model
                                            , @PathVariable long boardNo){

        model.addAttribute("boardNo", boardNo);

        return "th/board/boardReply";
    }
}
