package com.example.boardrest.domain.dto.member.in;

import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.OAuthProvider;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class JoinDTO {

    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String email;

    public Member toEntity(String profile) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return Member.builder()
                .userId(this.userId)
                .userPw(passwordEncoder.encode(this.userPw))
                .username(this.userName)
                .email(this.email)
                .nickname(this.nickname)
                .profileThumbnail(profile)
                .provider(OAuthProvider.LOCAL.getKey())
                .build();
    }

}
