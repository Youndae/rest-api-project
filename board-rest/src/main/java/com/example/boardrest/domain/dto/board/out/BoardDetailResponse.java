package com.example.boardrest.domain.dto.board.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponse {

    private String title;

    private String writer;

    private String writerId;

    private String content;

    private LocalDate createdAt;

    public BoardDetailResponse(String title, String writer, String writerId, String content, LocalDateTime createdAt) {
        this.title = title;
        this.writer = writer;
        this.writerId = writerId;
        this.content = content;
        this.createdAt = createdAt.toLocalDate();
    }
}
