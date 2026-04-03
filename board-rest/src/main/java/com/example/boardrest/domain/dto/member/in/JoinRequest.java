package com.example.boardrest.domain.dto.member.in;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRequest {

    private String userId;

    private String password;

    private String userName;

    private String nickname;

    private String email;

    private MultipartFile profile;

    @Builder
    public JoinRequest(
            String userId,
            String password,
            String userName,
            String nickname,
            String email,
            MultipartFile profile
    ) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
    }

    public boolean hasProfile() {
        return profile != null && !profile.isEmpty();
    }


}
