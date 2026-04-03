package com.example.boardrest.domain.dto.imageBoard.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardPatchDetail {

    private String writer;
    private String title;
    private String content;
}
