package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardController {


    private final HierarchicalBoardWebClient hierarchicalBoardWebClient;

    private final TokenService tokenService;

    @GetMapping("/boardList")
    public String hierarchicalBoardMain(Model model, Criteria cri) throws Exception {
        model.addAttribute("boardList", hierarchicalBoardWebClient.getHierarchicalBoardList(cri));

        return "th/board/boardList";
    }

    @GetMapping("/boardDetail/{boardNo}")
    public String hierarchicalBoardDetail(Model model
                                            , @PathVariable long boardNo) {
        HierarchicalBoardDTO dto = hierarchicalBoardWebClient.getHierarchicalBoardDetail(boardNo);

        model.addAttribute("board", dto);

        return "th/board/boardDetail";
    }

    @PatchMapping("/boardModify")
    public String hierarchicalBoardModifyProc(HttpServletRequest request, HttpServletResponse response){
        long responseVal = hierarchicalBoardWebClient.modifyPatch(request, response);

        if(responseVal == -1L)
            return "th/error/error";

        return "redirect:/board/boardDetail/" + responseVal;
    }

    @GetMapping("/boardModify/{boardNo}")
    public String hierarchicalBoardModify(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response) throws JsonProcessingException {

        log.info("modify boardNo : {}", boardNo);

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            return "th/member/loginForm";
        }

        HierarchicalBoardModifyDTO dto = hierarchicalBoardWebClient.getModifyData(boardNo, request, response);

        if(dto == null)
            return "th/error/error";

        model.addAttribute("modify", dto);

        return "th/board/boardModify";
    }

    @GetMapping("/boardInsert")
    public String hierarchicalBoardInsert(HttpServletRequest request, HttpServletResponse response){

        JwtDTO existsToken = tokenService.checkExistsToken(request, response);

        if(existsToken != null)
            return "th/board/boardInsert";
        else
            return "th/member/loginForm";

    }

    @PostMapping("/boardInsert")
    public String hierarchicalBoardInsertProc(HttpServletRequest request, HttpServletResponse response){


        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardInsert(request, response);

        log.info("controller insertProc response : {}", response);

        if(responseVal < 0)
            return "th/member/join";
        else
            return "redirect:/board/boardDetail/" + responseVal;
    }

    @GetMapping("/boardReply/{boardNo}")
    public String hierarchicalBoardReply(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response){

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null){
            return "th/member/loginForm";
        }


        model.addAttribute("boardNo", boardNo);

        return "th/board/boardReply";
    }

    @PostMapping("/boardReply")
    public String hierarchicalBoardReplyProc(HttpServletRequest request, HttpServletResponse response){
        log.info("boardReply Proc");

        log.info("title : {}, content : {}, boardNo : {}"
                , request.getParameter("boardTitle")
                , request.getParameter("boardContent")
                , request.getParameter("boardNo"));

        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardReply(request, response);

        log.info("responseVal : {}", responseVal);

        return "redirect:/board/boardDetail/" + responseVal;
    }

    @DeleteMapping("/boardDelete/{boardNo}")
    @ResponseBody
    public int deleteBoard(@PathVariable long boardNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        log.info("delete boardNo : {}", boardNo);

        return hierarchicalBoardWebClient.boardDelete(boardNo, request, response);
    }
}
