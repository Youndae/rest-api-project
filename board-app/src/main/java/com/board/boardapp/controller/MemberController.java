package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.HierarchicalBoardWebClient;
import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.MemberDTO;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberWebClient memberWebClient;

    private final TokenService tokenService;

    @GetMapping("/loginForm")
    public String login(HttpServletRequest request, HttpServletResponse response) {

        JwtDTO tokenDTO = tokenService.checkExistsToken(request, response);

        if(tokenDTO == null)
            return "th/member/loginForm";
        else
            return "redirect:/board/boardList";

    }

    @PostMapping("/login")
    @ResponseBody
    public int loginProc(@RequestBody Map<String, String> loginData
            , HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        System.out.println("loginProc");

        System.out.println("username : " + loginData.get("userId"));
        System.out.println("userPw : " + loginData.get("userPw"));

        return memberWebClient.loginProc(loginData, request, response);
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

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        log.info("logout controller");

        int result = memberWebClient.logout(request, response);

        if(result == 1){
            /**
             * 로그아웃 처리가 정상적으로 동작했다면
             * lsc, Authorization, Authorization_Refresh 쿠키 전체 삭제.
             */
            tokenService.deleteCookie(request, response);
            return "redirect:/board/boardList";
        }else{
            return "th/error/error";
        }
    }

}
