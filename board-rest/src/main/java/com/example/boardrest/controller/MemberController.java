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
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<Long> loginProc(@RequestBody Member member
                                        , HttpServletRequest request
                                        , HttpServletResponse response) {

        return new ResponseEntity<>(memberService.memberLogin(member, request, response), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public int logout(HttpServletRequest request
                    , HttpServletResponse response
                    , Principal principal){

        return memberService.logout(request, response, principal);
    }
}
