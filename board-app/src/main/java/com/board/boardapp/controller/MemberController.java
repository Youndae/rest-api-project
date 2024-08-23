package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.MemberWebClient;
import com.board.boardapp.domain.dto.LoginDTO;
import com.board.boardapp.domain.dto.JoinDTO;
import com.board.boardapp.domain.dto.ProfileDTO;
import com.board.boardapp.domain.dto.UserStatusDTO;
import com.board.boardapp.service.CookieService;
import com.board.boardapp.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
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

    private final CookieService cookieService;

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
    public String loginProc(@RequestBody Map<String, String> loginData
                            , HttpServletRequest request
                            , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return memberWebClient.loginProc(loginData, cookieMap, response);
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
    public String joinProc(JoinDTO joinDTO
            , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail){

        return memberWebClient.joinProc(joinDTO, profileThumbnail);
    }

    @GetMapping("/check-userid")
    @ResponseBody
    public String checkUserId(@RequestParam("userId") String userId){

        return memberWebClient.checkUserId(userId);
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname, HttpServletRequest request) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return memberWebClient.checkNickname(nickname, cookieMap);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        String result = memberWebClient.logout(cookieMap, response);

        if(result.equals("SUCCESS"))
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

    /**
     *
     * 코드 개선 필요.
     */
    @GetMapping("/profile")
    public String getProfile(HttpServletRequest request, HttpServletResponse response, Model model) {
        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        ProfileDTO dto = memberWebClient.getProfile(cookieMap, response);

        model.addAttribute("data", dto);

        return "th/member/profile";
    }

    @PatchMapping("/join-oauth-profile")
    @ResponseBody
    public String patchProfile(@RequestParam(value = "nickname") String nickname
                            , @RequestParam(value = "profileThumbnail", required = false) MultipartFile profileThumbnail
                            , @RequestParam(value = "deleteProfileThumbnail", required = false) String deleteProfileThumbnail
                            , HttpServletRequest request
                            , HttpServletResponse response) {

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return memberWebClient.patchProfile(nickname, profileThumbnail, deleteProfileThumbnail, cookieMap, response);
    }
}
