package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.dto.JwtDTO;
import com.board.boardapp.dto.LoginDTO;
import com.board.boardapp.dto.MemberDTO;
import com.board.boardapp.dto.UserStatusDTO;
import com.board.boardapp.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String login(HttpServletRequest request, Model model) {

        boolean checkToken = tokenService.checkExistsToken(request);
        LoginDTO dto = new LoginDTO(new UserStatusDTO(false, null));

        if(!checkToken) {
            model.addAttribute("data", dto);
            return "th/member/loginForm";
        }else
            return "redirect:/board/boardList";

    }

    @PostMapping("/login")
    @ResponseBody
    public int loginProc(@RequestBody Map<String, String> loginData
                        , HttpServletRequest request
                        , HttpServletResponse response) {


        return memberWebClient.loginProc(loginData, request, response);
    }

    @GetMapping("/join")
    public String join(HttpServletRequest request, Model model){

        boolean checkToken = tokenService.checkExistsToken(request);
        LoginDTO dto = new LoginDTO(new UserStatusDTO(false, null));

        if(checkToken)
            return "redirect:/board/boardList";
        else {
            model.addAttribute("data", dto);
            return "th/member/join";
        }
    }

    @PostMapping("/joinProc")
    @ResponseBody
    public Long joinProc(MemberDTO memberDTO){
        log.info("joinProc");

        return memberWebClient.joinProc(memberDTO);
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public Long checkUserId(@RequestParam("userId") String userId){

        log.info("checkUserId");

        return memberWebClient.checkUserId(userId);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        Long result = memberWebClient.logout(request, response);

        if(result == 1L)
            return "redirect:/board/boardList";
        else
            return "th/error/error";
    }

}
