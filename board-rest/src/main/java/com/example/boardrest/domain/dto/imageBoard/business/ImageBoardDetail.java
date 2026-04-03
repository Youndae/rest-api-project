package com.example.boardrest.domain.dto.imageBoard.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardDetail {

    private String title;
    private String content;
    private String writer;
    private String writerId;
    private LocalDateTime createdAt;
}
