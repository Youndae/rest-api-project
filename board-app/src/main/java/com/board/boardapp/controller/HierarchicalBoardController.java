package com.board.boardapp.controller;

import com.board.boardapp.ExceptionHandle.CustomAccessDeniedException;
import com.board.boardapp.ExceptionHandle.CustomNotFoundException;
import com.board.boardapp.ExceptionHandle.ErrorCode;
import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.domain.dto.*;
import com.board.boardapp.domain.dto.hBoard.in.HierarchicalBoardInsertDTO;
import com.board.boardapp.domain.dto.hBoard.in.HierarchicalBoardReplyInsertDTO;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
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

    private final CookieService cookieService;

    @GetMapping("/")
    public String getList(Model model
                        , Criteria cri
                        , HttpServletRequest request
                        , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        PaginationListDTO<HierarchicalBoardDTO> dto = hierarchicalBoardWebClient.getList(cri, cookieMap, response);

        model.addAttribute("data", dto);

        return "th/board/boardList";
    }

    @GetMapping("/{boardNo}")
    public String boardDetail(Model model
                            , @PathVariable long boardNo
                            , HttpServletRequest request
                            , HttpServletResponse response) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        BoardDetailAndModifyDTO<HierarchicalBoardDTO> dto = hierarchicalBoardWebClient
                                                                .getDetail(boardNo, cookieMap, response);

        model.addAttribute("data", dto);

        return "th/board/boardDetail";
    }

    @PatchMapping("/{boardNo}")
    public String patchBoard(@PathVariable long boardNo
                             , HierarchicalBoardInsertDTO dto
                            , HttpServletRequest request
                            , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        long responseVal = hierarchicalBoardWebClient.patchBoard(boardNo, dto, cookieMap, response);

        return "redirect:/board/" + responseVal;
    }

    @DeleteMapping("/{boardNo}")
    @ResponseBody
    public String deleteBoard(@PathVariable long boardNo
                            , HttpServletRequest request
                            , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return hierarchicalBoardWebClient.deleteBoard(boardNo, cookieMap, response);
    }

    @GetMapping("/patch/{boardNo}")
    public String getPatchDetail(Model model
                                , @PathVariable long boardNo
                                , HttpServletRequest request
                                , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        BoardDetailAndModifyDTO<HierarchicalBoardModifyDTO> dto = hierarchicalBoardWebClient
                                                                    .getPatchDetail(boardNo, cookieMap, response);

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
    public String insertBoard(@ModelAttribute HierarchicalBoardInsertDTO dto
                                ,HttpServletRequest request
                                , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        long responseVal = hierarchicalBoardWebClient.postBoard(dto, cookieMap, response);

        return "redirect:/board/" + responseVal;
    }

    @GetMapping("/reply/{boardNo}")
    public String getReplyDetail(Model model
                                        , @PathVariable long boardNo
                                        , HttpServletRequest request
                                        , HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        BoardDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> dto = hierarchicalBoardWebClient
                                                                .getReplyDetail(cookieMap, response, boardNo);

        model.addAttribute("data", dto);
        model.addAttribute("bno", boardNo);

        return "th/board/boardReply";
    }

    @PostMapping("/reply")
    public String replyInsertBoard(@ModelAttribute HierarchicalBoardReplyInsertDTO dto
                                    , HttpServletRequest request
                                    , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        long responseVal = hierarchicalBoardWebClient.postReply(dto, cookieMap, response);

        return "redirect:/board/" + responseVal;
    }

}
