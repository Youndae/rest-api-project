package com.example.boardrest.domain.dto.member.in;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class ProfileDTO {

    private String nickname;

    private MultipartFile profileThumbnail;

    private String deleteProfile;
}
