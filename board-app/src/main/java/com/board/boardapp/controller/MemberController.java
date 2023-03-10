package com.board.boardapp.controller;

import com.board.boardapp.connection.RestCallWebClient;
import com.board.boardapp.dto.Member;
import com.board.boardapp.dto.MemberDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final RestCallWebClient restCallWebClient;

    @GetMapping("/loginForm")
    public String login() {
        return "th/member/loginForm";
    }

    @GetMapping("/join")
    public String join(){
        return "th/member/join";
    }

    @PostMapping("/login")
    public String loginProc(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        System.out.println("loginProc");

        System.out.println("username : " + request.getParameter("userId"));
        System.out.println("userPw : " + request.getParameter("userPw"));

        restCallWebClient.loginProc(request, response);

        return "redirect:/board/boardList";
    }

}
