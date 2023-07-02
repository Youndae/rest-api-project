package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.MemberDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.dto.JwtDTO;
import com.example.boardrest.repository.MemberRepository;
import com.example.boardrest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @PostMapping("/join-proc")
    public int joinProc(@RequestBody MemberDTO dto){
        log.info("join Process");

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
    public ResponseEntity<JwtDTO> loginProc(@RequestBody Member member, HttpServletRequest request) throws Exception {

        log.info("controller login");

        log.info("controller userId : " + member.getUserId());
        log.info("controller userPw : " + member.getUserPw());
//        authenticationFilter.attemptAuthentication(request, response);


        return new ResponseEntity<>(memberService.memberLogin(member), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public int logout(HttpServletRequest request, Principal principal){

        log.info("logout");

        return memberService.logout(request, principal);

    }
}
