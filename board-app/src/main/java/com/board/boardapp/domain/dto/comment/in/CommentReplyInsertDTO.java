package com.board.boardapp.domain.dto.comment.in;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentReplyInsertDTO extends CommentInsertDTO {

    private Long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;
}
