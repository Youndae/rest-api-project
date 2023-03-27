package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.dto.MemberDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberWebClient memberWebClient;

    @GetMapping("/loginForm")
    public String login() {
        return "th/member/loginForm";
    }

    @PostMapping("/login")
    public String loginProc(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        System.out.println("loginProc");

        System.out.println("username : " + request.getParameter("userId"));
        System.out.println("userPw : " + request.getParameter("userPw"));

        memberWebClient.loginProc(request, response);

        return "redirect:/board/boardList";
    }

    @GetMapping("/join")
    public String join(){
        return "th/member/join";
    }

    @PostMapping("/joinProc")
    @ResponseBody
    public int joinProc(MemberDTO memberDTO){
        log.info("joinProc");

        log.info("join dto : {}", memberDTO);

        return memberWebClient.joinProc(memberDTO);
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public int checkUserId(@RequestParam("userId") String userId){

        log.info("checkUserId");
        log.info("userId : {}", userId);

        return memberWebClient.checkUserId(userId);
    }

}
