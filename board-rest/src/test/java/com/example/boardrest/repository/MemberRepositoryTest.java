package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Member;
import org.assertj.core.api.Assertions;
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

        Assertions.assertThat(member.getNickname().equals("cocomozzi"));

    }
}