package com.example.boardrest.domain.dto;

import com.example.boardrest.domain.entity.Member;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PrincipalDTO {

    private Long id;

    private String userId;

    private String nickname;

    private String provider;

    public Member toMemberEntity() {

        return Member.builder()
                .id(this.id)
                .userId(this.userId)
                .nickname(this.nickname)
                .provider(this.provider)
                .build();
    }
}
