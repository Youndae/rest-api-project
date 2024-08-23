package com.example.boardrest.domain.dto.comment.out;

import lombok.*;

import java.time.LocalDate;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BoardCommentDTO {
//
    private long commentNo;

    private String nickname;

    private LocalDate commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

}
