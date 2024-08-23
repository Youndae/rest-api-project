package com.example.boardrest.domain.dto.member.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDTO {

    private String nickname;

    private String profileThumbnail;

}
