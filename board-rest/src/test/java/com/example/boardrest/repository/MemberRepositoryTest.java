package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("닉네임 중복 여부 체크")
    void nicknameCheck() {
        Member member = memberRepository.findByNickname("cocomozzi");

        Assertions.assertEquals(member.getNickname(), "cocomozzi");

    }

    @Test
    @DisplayName("로컬 로그인 테스트")
    void localLoginTest() {
        String cocoId = "coco";
        String cocoNickname = "코코";
        String testerId = "testuser1";


        Member cocoData = memberRepository.findByLoginUserId(cocoId);
        Member testerData = memberRepository.findByLoginUserId(testerId);

        Assertions.assertEquals(cocoData.getNickname(), cocoNickname);
        Assertions.assertNull(testerData);
    }
}