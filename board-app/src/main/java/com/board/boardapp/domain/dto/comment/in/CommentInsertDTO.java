package com.board.boardapp.domain.dto.comment.in;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentInsertDTO {

    private Long imageNo;

    private Long boardNo;

    private String commentContent;
}
