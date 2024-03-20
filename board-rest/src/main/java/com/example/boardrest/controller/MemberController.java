package com.example.boardrest.controller;

import com.example.boardrest.config.jwt.JwtProperties;
import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @PostMapping("/join-proc")
    public int joinProc(@RequestBody MemberDTO dto){

        return memberService.memberJoinProc(dto);
    }

    @GetMapping("/check-user-id")
    public int checkUserId(@RequestParam("userId") String userId){

        if(memberRepository.findByUserId(userId) != null)
            return 1;
        else
            return 0;
    }

    @PostMapping("/login")
    public ResponseEntity<Long> loginProc(@RequestBody Member member, HttpServletRequest request, HttpServletResponse response) throws Exception {

        System.out.println("member : " + member);

        return new ResponseEntity<>(memberService.memberLogin(member, request, response), HttpStatus.OK);
    }

    /*@PostMapping("/login")
    public ResponseEntity loginProc2(@RequestBody Member member
                                    , HttpServletRequest request
                                    , HttpServletResponse response){
        System.out.println("loginProc2 member : " + member);

//        List<Cookie> cookies = new ArrayList<>();

        *//*Cookie atCookie = new Cookie(JwtProperties.ACCESS_HEADER_STRING, "atTestValue");
        atCookie.setPath("/");
        atCookie.setHttpOnly(true);
        atCookie.setSecure(true);
        atCookie.setMaxAge(60 * 60 * 2);

        Cookie rtCookie = new Cookie(JwtProperties.REFRESH_HEADER_STRING, "rtTestValue");
        atCookie.setPath("/");
        atCookie.setHttpOnly(true);
        atCookie.setSecure(true);
        atCookie.setMaxAge(60 * 60 * 24 * 14);*//*

//        cookies.add(atCookie);
//        cookies.add(rtCookie);

//        response.addCookie(atCookie);
//        response.addCookie(rtCookie);


        ResponseCookie at = ResponseCookie.from(JwtProperties.ACCESS_HEADER_STRING, "atTestValue")
                .secure(true)
                .maxAge(Duration.ofHours(2L))
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie rt = ResponseCookie.from(JwtProperties.REFRESH_HEADER_STRING, "rtTestValue")
                .secure(true)
                .maxAge(Duration.ofDays(14L))
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", at.toString());
        response.addHeader("Set-Cookie", rt.toString());

        return new ResponseEntity(HttpStatus.OK);
    }*/

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public int logout(HttpServletRequest request, Principal principal){

        return memberService.logout(request, principal);
    }
}
