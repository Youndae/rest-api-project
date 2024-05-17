package com.example.boardrest.service;

import com.example.boardrest.domain.dto.PrincipalDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrincipalServiceTest {

    @Autowired
    private PrincipalService principalService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("principal을 통한 PrincipalDTO 생성 및 반환 테스트")
    void checkPrincipalTest() {
        String userId = "coco";

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return userId;
            }
        };

        Member member = memberRepository.findByUserId(userId);

        PrincipalDTO principalDTO1 = PrincipalDTO.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .provider(member.getProvider())
                .build();

        PrincipalDTO principalDTO2 = principalService.checkPrincipal(principal);

        Assertions.assertEquals(principalDTO1, principalDTO2);
    }
}