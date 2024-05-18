package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.dto.LoginDTO;
import com.board.boardapp.dto.JoinDTO;
import com.board.boardapp.dto.ProfileDTO;
import com.board.boardapp.dto.UserStatusDTO;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public Long joinProc(JoinDTO joinDTO
            , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail){

        log.info("join :: dto : {}", joinDTO);
        log.info("join :: file : {}", profileThumbnail);



        return memberWebClient.joinProc(joinDTO, profileThumbnail);
    }

    @GetMapping("/check-userid")
    @ResponseBody
    public Long checkUserId(@RequestParam("userId") String userId){

        return memberWebClient.checkUserId(userId);
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public Long checkNickname(@RequestParam("nickname") String nickname, HttpServletRequest request) {
        return memberWebClient.checkNickname(nickname, request);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Long result = memberWebClient.logout(request, response);

        if(result == 1L)
            return "redirect:/board/";
        else
            return "th/error/error";
    }

    @GetMapping("/{provider}")
    public void oAuthLogin(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String url = "";

        if(provider.equals("google"))
           url = "http://localhost:8080/oauth2/authorization/google";
        else if(provider.equals("naver"))
            url = "http://localhost:8080/oauth2/authorization/naver";
        else if(provider.equals("kakao"))
            url = "http://localhost:8080/oauth2/authorization/kakao";

        response.sendRedirect(url);
    }

    @GetMapping("/oAuth")
    public String oAuthSuccess() {

        return "th/member/oAuthSuccess";
    }

    @GetMapping("/profile")
    public String getProfile(HttpServletRequest request, HttpServletResponse response, Model model) {

        ProfileDTO dto = memberWebClient.getProfile(request, response);
        dto.setUserStatus(new UserStatusDTO(true, null));
        dto.setBtnText(dto.getNickname() == null ? "등록" : "수정");

        log.info("profile : {}", dto);
        model.addAttribute("data", dto);

        return "th/member/profile";
    }

    @PatchMapping("/join-oauth-profile")
    @ResponseBody
    public Long patchProfile(@RequestParam(value = "nickname") String nickname
                            , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail
                            , @RequestParam(value = "deleteProfileThumbnail", required = false) String deleteProfileThumbnail
                            , HttpServletRequest request
                            , HttpServletResponse response) {

        return memberWebClient.patchProfile(nickname, profileThumbnail, deleteProfileThumbnail, request, response);
    }
}
