package com.example.boardrest.service;

import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.PrincipalDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService{

    private final MemberRepository memberRepository;

    @Override
    public PrincipalDTO checkPrincipal(Principal principal) {

        try{
            Member member = getMemberEntity(principal);

            return PrincipalDTO.builder()
                    .id(member.getId())
                    .userId(member.getUserId())
                    .nickname(member.getNickname())
                    .provider(member.getProvider())
                    .build();
        }catch (Exception e){
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        }

    }

    @Override
    public String getNicknameToPrincipal(Principal principal) {
        if (principal != null){
            Member member = getMemberEntity(principal);

            return member.getNickname();
        }

        return null;
    }

    private Member getMemberEntity(Principal principal) {
        return memberRepository.findByUserId(principal.getName());
    }
}
