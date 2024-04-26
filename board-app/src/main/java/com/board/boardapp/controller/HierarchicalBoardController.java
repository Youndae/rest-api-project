package com.board.boardapp.controller;

import com.board.boardapp.ExceptionHandle.CustomAccessDeniedException;
import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.dto.*;
import com.board.boardapp.service.TokenService;
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

    @GetMapping("/")
    public String getList(Model model
                        , Criteria cri
                        , HttpServletRequest request
                        , HttpServletResponse response) {

        System.out.println("boardList");
        HierarchicalBoardListDTO dto = hierarchicalBoardWebClient.getList(cri, request, response);

        model.addAttribute("data", dto);

        return "th/board/boardList";
    }

    @GetMapping("/{boardNo}")
    public String boardDetail(Model model
                            , @PathVariable long boardNo
                            , HttpServletRequest request
                            , HttpServletResponse response) {
        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = hierarchicalBoardWebClient
                                                                .getDetail(boardNo, request, response);

        model.addAttribute("data", dto);

        return "th/board/boardDetail";
    }

    @PatchMapping("/{boardNo}")
    public String patchBoard(@PathVariable long boardNo
                            , HttpServletRequest request
                            , HttpServletResponse response){

        long responseVal = hierarchicalBoardWebClient.patchBoard(boardNo, request, response);

        return "redirect:/board/" + responseVal;
    }

    @DeleteMapping("/{boardNo}")
    @ResponseBody
    public Long deleteBoard(@PathVariable long boardNo
            , HttpServletRequest request
            , HttpServletResponse response){

        System.out.println("deleteNo : " + boardNo);

        return hierarchicalBoardWebClient.deleteBoard(boardNo, request, response);
    }

    @GetMapping("/patch/{boardNo}")
    public String getPatchDetail(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response) {
        BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> dto = hierarchicalBoardWebClient
                                                                    .getPatchDetail(boardNo, request, response);

        if(dto == null)
            throw new CustomNotFoundException(ErrorCode.DATA_NOT_FOUND);

        model.addAttribute("data", dto);

        return "th/board/boardModify";
    }

    @GetMapping("/post")
    public String getInsertPage(HttpServletRequest request, Model model){
        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, "userNotFound");

        LoginDTO dto = new LoginDTO(new UserStatusDTO(true));
        model.addAttribute("data", dto);

        return "th/board/boardInsert";
    }

    @PostMapping("/")
    public String insertBoard(HttpServletRequest request, HttpServletResponse response){
        long responseVal = hierarchicalBoardWebClient.postBoard(request, response);

        return "redirect:/board/" + responseVal;
    }

    @GetMapping("/reply/{boardNo}")
    public String getReplyDetail(Model model
                                        , @PathVariable long boardNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response){
        BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> dto = hierarchicalBoardWebClient
                                                                .getReplyDetail(request, response, boardNo);

        model.addAttribute("data", dto);
        model.addAttribute("bno", boardNo);

        return "th/board/boardReply";
    }

    @PostMapping("/reply")
    public String replyInsertBoard(HttpServletRequest request, HttpServletResponse response){
        long responseVal = hierarchicalBoardWebClient.postReply(request, response);

        return "redirect:/board/" + responseVal;
    }

}
