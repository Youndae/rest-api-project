package com.example.boardrest.domain.dto.board.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPatchDetailResponse {
    private String title;
    private String content;
}
