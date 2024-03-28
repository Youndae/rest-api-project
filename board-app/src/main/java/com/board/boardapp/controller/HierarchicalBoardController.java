package com.board.boardapp.controller;

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

    @GetMapping("/boardList")
    public String hierarchicalBoardMain(Model model
                                        , Criteria cri
                                        , HttpServletRequest request
                                        , HttpServletResponse response) {
        HierarchicalBoardListDTO dto = hierarchicalBoardWebClient.getHierarchicalBoardList(cri, request, response);

        model.addAttribute("data", dto);

        return "th/board/boardList";
    }

    @GetMapping("/boardDetail/{boardNo}")
    public String hierarchicalBoardDetail(Model model
                                            , @PathVariable long boardNo
                                            , HttpServletRequest request
                                            , HttpServletResponse response) {
        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = hierarchicalBoardWebClient
                                                                .getHierarchicalBoardDetail(boardNo, request, response);

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
        BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> dto = hierarchicalBoardWebClient
                                                                    .getModifyData(boardNo, request, response);

        if(dto == null)
            return "th/error/error";

        model.addAttribute("data", dto);

        return "th/board/boardModify";
    }

    @GetMapping("/boardInsert")
    public String hierarchicalBoardInsert(HttpServletRequest request, Model model){
        boolean checkToken = tokenService.checkExistsToken(request);

        if(!checkToken)
            return "redirect:/member/loginForm";

        LoginDTO dto = new LoginDTO(new UserStatusDTO(true));
        model.addAttribute("data", dto);

        return "th/board/boardInsert";
    }

    @PostMapping("/boardInsert")
    public String hierarchicalBoardInsertProc(HttpServletRequest request, HttpServletResponse response){
        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardInsert(request, response);

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
        BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> dto = hierarchicalBoardWebClient
                                                                .getHierarchicalBoardReplyInfo(request, response, boardNo);

        model.addAttribute("data", dto);

        return "th/board/boardReply";
    }

    @PostMapping("/boardReply")
    public String hierarchicalBoardReplyProc(HttpServletRequest request, HttpServletResponse response){
        long responseVal = hierarchicalBoardWebClient.hierarchicalBoardReply(request, response);

        return "redirect:/board/boardDetail/" + responseVal;
    }

    @DeleteMapping("/boardDelete/{boardNo}")
    @ResponseBody
    public Long deleteBoard(@PathVariable long boardNo
                            , HttpServletRequest request
                            , HttpServletResponse response){

        return hierarchicalBoardWebClient.boardDelete(boardNo, request, response);
    }
}
