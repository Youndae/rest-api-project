package com.example.boardrest.service;

import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.CustomNotFoundException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.auth.PrincipalDTO;
import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalServiceImpl implements PrincipalService{

    private final MemberRepository memberRepository;

    @Override
    public Member getMemberByUserId(String userId) {

        Member member = memberRepository.findByUserId(userId);

        if(member == null){
            log.warn("PrincipalService.getMemberByUserId :: member is null. userId={}", userId);
            throw new CustomNotFoundException(ErrorCode.BAD_REQUEST, "member is null");
        }

        return member;
    }

    @Override
    public void validateUser(String writer, String userId) {
        if(!writer.equals(userId))
            throw new CustomAccessDeniedException(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
    }
}
