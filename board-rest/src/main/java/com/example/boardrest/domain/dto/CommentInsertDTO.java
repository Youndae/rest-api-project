package com.example.boardrest.domain.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentInsertDTO {

    private long commentNo;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    private long boardNo;

    private long imageNo;


    public void setCommentNo(long commentNo) {
        this.commentNo = commentNo;
    }

    public void setCommentGroupNo(long commentGroupNo) {
        this.commentGroupNo = commentGroupNo;
    }

    public void setCommentIndent(int commentIndent) {
        this.commentIndent = commentIndent;
    }

    public void setCommentUpperNo(String commentUpperNo) {
        this.commentUpperNo = commentUpperNo;
    }
}
