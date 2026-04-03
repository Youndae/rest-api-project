package com.example.boardrest.domain.dto.board.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardPatchDetail {

    private String userId;
    private String title;
    private String content;
}
