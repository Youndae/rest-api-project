package com.board.boardapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/loginForm")
    public String login() {
        return "th/member/loginForm";
    }

    @GetMapping("/join")
    public String join(){
        return "th/member/join";
    }

}
