package com.example.boardrest.controller;

import com.example.boardrest.config.jwt.JwtAuthenticationFilter;
import com.example.boardrest.config.jwt.JwtAuthorizationFilter;
import com.example.boardrest.domain.Member;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @PostMapping("/join-proc")
    public int joinProc(Member member){
        log.info("join Process");

        return memberService.memberJoinProc(member);
    }

    @PostMapping("/check-user-id")
    public int checkUserId(Member member){

        if(memberRepository.findByUserId(member.getUserId()) != null)
            return 1;
        else
            return 0;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> loginProc(@RequestBody Member member, HttpServletRequest request) throws Exception {

        log.info("controller login");

        log.info("controller userId : " + member.getUserId());
        log.info("controller userPw : " + member.getUserPw());
//        authenticationFilter.attemptAuthentication(request, response);


        return new ResponseEntity<>(memberService.memberLogin(member), HttpStatus.OK);
    }
}
