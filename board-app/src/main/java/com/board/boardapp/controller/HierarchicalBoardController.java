package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.dto.Criteria;
import com.board.boardapp.dto.HierarchicalBoardDetailDTO;
import com.board.boardapp.dto.HierarchicalBoardModifyDTO;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
                                            , Criteria cri) throws JsonProcessingException {

        model.addAttribute("boardList", hierarchicalBoardWebClient.getHierarchicalBoardList(cri));

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

        HierarchicalBoardDetailDTO dto = hierarchicalBoardWebClient.getHierarchicalBoardDetail(boardNo, request, response);

        model.addAttribute("board", dto);

        log.info("boardDetail uid : {}", dto.getUid());

        log.info("uid : {} ", response.getHeader("uid"));

        model.addAttribute("uid", response.getHeader("uid"));

        return "th/board/boardDetail";
    }

    @PatchMapping("/boardModify")
    public String hierarchicalBoardModifyProc(HttpServletRequest request, HttpServletResponse response){
        log.info("title : {}, content : {}, boardNo : {}", request.getParameter("boardTitle"), request.getParameter("boardContent"), request.getParameter("boardNo"));

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

        /**
         * exception Handler 구현 전 임시조치
         */
        if(responseVal < 0)
            return "th/member/join";
        else
            return "redirect:/board/boardDetail/" + responseVal;
    }

    @GetMapping("/boardReply/{boardNo}")
    public String hierarchicalBoardReply(Model model
                                            , @PathVariable long boardNo){
        /**
         * @Data
         * boardNo
         * groupNo
         * Indent
         * upperNo
         *
         * or
         *
         * boardNo만 받아서 페이지 바로 열어주고
         * 작성 버튼 눌렀을 때 기존글 boardNo를 같이 보내줘서
         * 서버에서 boardNo에 해당하는 데이터 가져 온 뒤
         * 그 데이터를 토대로 필요한 데이터를 만들어 save하는 방법도 가능.
         *
         * 장단점.
         * boardNo를 받아 서버에서 필요 데이터를 가져올 때 사용자와 글 작성자를 비교해
         * 다른 경우 걸러내는 작업이 가능.
         * 물론 boardNo만 받아서 페이지 바로 열어주더라도 작성 처리시에 검증을 할 수 있지만
         * 먼저 필요 데이터를 받는 경우 검증을 한번 더 해서 이중으로 처리가 가능하다는 장점이 있음.
         * 그래도?
         * 항상 전자의 방식대로 했으니 이번에는 후자의 방법으로.
         */

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
