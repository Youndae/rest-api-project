package com.example.boardrest.domain.dto.member.in;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthJoinRequest {

    private String nickname;

    private MultipartFile profile;

    public boolean hasProfile() {
        return profile != null && !profile.isEmpty();
    }
}
