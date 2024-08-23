package com.example.boardrest.domain.dto.comment.in;

import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInsertDTO {

    private Long boardNo;

    private Long imageNo;

    private String commentContent;

    private Long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    public Comment toEntity(Member member) {
        return Comment.builder()
                .member(member)
                .commentContent(this.commentContent)
                .build();
    }
}
