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
    public String hierarchicalBoardMain(Model model, Criteria cri, HttpServletRequest request, HttpServletResponse response) {
        HierarchicalBoardListDTO dto = hierarchicalBoardWebClient.getHierarchicalBoardList(cri, request, response);

        model.addAttribute("data", dto);

        return "th/board/boardList";
    }

    @GetMapping("/boardDetail/{boardNo}")
    public String hierarchicalBoardDetail(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response) {
        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = hierarchicalBoardWebClient.getHierarchicalBoardDetail(boardNo, request, response);

        model.addAttribute("data", dto);

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
                                            , HttpServletResponse response) {

        log.info("modify boardNo : {}", boardNo);

//        boolean checkToken = tokenService.checkExistsToken(request, response);

//        if(checkToken)
//            return "th/member/loginForm";

        BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> dto = hierarchicalBoardWebClient.getModifyData(boardNo, request, response);

        if(dto == null)
            return "th/error/error";

        model.addAttribute("data", dto);

        return "th/board/boardModify";
    }

    @GetMapping("/boardInsert")
    public String hierarchicalBoardInsert(HttpServletRequest request, Model model){

        boolean checkToken = tokenService.checkExistsToken(request);

        if(checkToken) {
            LoginDTO dto = new LoginDTO(new UserStatusDTO(true));
            model.addAttribute("data", dto);
            return "th/board/boardInsert";
        }else
            return "redirect:/member/loginForm";

    }

    @PostMapping("/boardInsert")
    public String hierarchicalBoardInsertProc(HttpServletRequest request
            , HttpServletResponse response){


        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardInsert(request, response);

        log.info("controller insertProc response : {}", response);

        if(responseVal < 0)
            return "th/error/error";
        else
            return "redirect:/board/boardDetail/" + responseVal;
    }

    @GetMapping("/boardReply/{boardNo}")
    public String hierarchicalBoardReply(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response){

        /*boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "th/member/loginForm";

        model.addAttribute("boardNo", boardNo);*/

        BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> dto = hierarchicalBoardWebClient.getHierarchicalBoardReplyInfo(request, response, boardNo);

        model.addAttribute("data", dto);

        return "th/board/boardReply";
    }

    @PostMapping("/boardReply")
    public String hierarchicalBoardReplyProc(HttpServletRequest request, HttpServletResponse response){
        log.info("boardReply Proc");

        log.info("title : {}, content : {}, boardNo : {}, indent : {}, groupNo : {}, upperNo : {}"
                , request.getParameter("boardTitle")
                , request.getParameter("boardContent")
                , request.getParameter("boardNo")
                , request.getParameter("boardIndent")
                , request.getParameter("boardGroupNo")
                , request.getParameter("boardUpperNo"));

        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardReply(request, response);

        log.info("responseVal : {}", responseVal);

        return "redirect:/board/boardDetail/" + responseVal;
    }

    @DeleteMapping("/boardDelete/{boardNo}")
    @ResponseBody
    public Long deleteBoard(@PathVariable long boardNo
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        log.info("delete boardNo : {}", boardNo);

        return hierarchicalBoardWebClient.boardDelete(boardNo, request, response);
    }
}
