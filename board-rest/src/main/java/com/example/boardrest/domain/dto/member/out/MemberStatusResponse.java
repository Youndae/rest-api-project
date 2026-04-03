package com.example.boardrest.domain.dto.member.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberStatusResponse {
    private String userId;
    private String role;
}
