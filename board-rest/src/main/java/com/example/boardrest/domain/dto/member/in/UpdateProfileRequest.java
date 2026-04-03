package com.example.boardrest.domain.dto.member.in;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProfileRequest {

    private String nickname;

    private String email;

    private MultipartFile profile;

    private String deleteProfile;

    @Builder
    public UpdateProfileRequest(
            String nickname,
            String email,
            MultipartFile profile,
            String deleteProfile
    ) {
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.deleteProfile = deleteProfile;
    }

    public boolean hasProfile() {
        return profile != null && !profile.isEmpty();
    }

    public boolean hasDeleteProfile() {
        return deleteProfile != null;
    }
}
