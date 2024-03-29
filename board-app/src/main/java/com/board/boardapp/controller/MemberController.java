package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.dto.LoginDTO;
import com.board.boardapp.dto.MemberDTO;
import com.board.boardapp.dto.UserStatusDTO;
import com.board.boardapp.service.TokenService;
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

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        boolean checkToken = tokenService.checkExistsToken(request);

        if(checkToken)
            return "redirect:/board/";

        LoginDTO dto = new LoginDTO(new UserStatusDTO(false, null));
        model.addAttribute("data", dto);

        return "th/member/loginForm";
    }

    @PostMapping("/login")
    @ResponseBody
    public Long loginProc(@RequestBody Map<String, String> loginData
                            , HttpServletRequest request
                            , HttpServletResponse response) {

        return memberWebClient.loginProc(loginData, request, response);
    }

    @GetMapping("/join")
    public String join(HttpServletRequest request, Model model){
        boolean checkToken = tokenService.checkExistsToken(request);

        if(checkToken)
            return "redirect:/board/";

        LoginDTO dto = new LoginDTO(new UserStatusDTO(false, null));
        model.addAttribute("data", dto);

        return "th/member/join";
    }

    @PostMapping("/join")
    @ResponseBody
    public Long joinProc(MemberDTO memberDTO){

        return memberWebClient.joinProc(memberDTO);
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public Long checkUserId(@RequestParam("userId") String userId){

        return memberWebClient.checkUserId(userId);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Long result = memberWebClient.logout(request, response);

        if(result == 1L)
            return "redirect:/board/";
        else
            return "th/error/error";
    }

}
