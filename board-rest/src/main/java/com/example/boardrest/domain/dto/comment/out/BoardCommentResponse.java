package com.example.boardrest.domain.dto.comment.out;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class BoardCommentResponse {

    private long id;

    private String writer;

    private String writerId;

    private LocalDate createdAt;

    private String content;

    private int indent;

    private boolean isDeleted;


    public BoardCommentResponse(long id, String nickname, String userId, LocalDateTime createdAt, String content, int indent, LocalDateTime deletedAt) {
        this.id = id;
        this.writer = deletedAt == null ? nickname : "";
        this.writerId = deletedAt == null ? userId : "";
        this.createdAt = createdAt.toLocalDate();
        this.content = deletedAt == null ? content : "삭제된 댓글입니다.";
        this.indent = indent;
        this.isDeleted = deletedAt != null;
    }
}
