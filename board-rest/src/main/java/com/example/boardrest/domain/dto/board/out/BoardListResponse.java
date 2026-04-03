package com.example.boardrest.domain.dto.board.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardListResponse {

    private long id;

    private String title;

    private String writer;

    private LocalDate createdAt;

    private int indent;

    public BoardListResponse(long id, String title, String writer, LocalDateTime created_at, int indent) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.createdAt = created_at.toLocalDate();
        this.indent = indent;
    }
}
