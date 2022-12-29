package com.example.boardrest.service;

import com.example.boardrest.domain.Member;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final BCryptPasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    // 사용자 회원가입(체크 후 메소드 호출로 save 처리)
    @Override
    public int memberJoinProc(Member member) {
        if(member.getUserId() == null || member.getUserPw().length() == 0 || member.getUserName() == null)
            return 0;
        else
            return joinMember(member);
    }

    // 사용자 데이터 save
    private int joinMember(Member member){
        try{
            Member memberEntity = Member.builder()
                    .userId(member.getUserId())
                    .userPw(passwordEncoder.encode(member.getUserPw()))
                    .userName(member.getUserName())
                    .build();

            memberRepository.save(memberEntity);
            log.info("join success");
            return 1;
        }catch (Exception e){
            log.info("failed Join");
            return 0;
        }
    }
}
