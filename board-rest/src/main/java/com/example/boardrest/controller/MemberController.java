package com.example.boardrest.controller;

import com.example.boardrest.domain.Member;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
