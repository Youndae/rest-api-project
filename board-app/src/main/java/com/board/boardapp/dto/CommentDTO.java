package com.board.boardapp.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private long commentNo;

    private String userId;

    private Date commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    private long boardNo;

    private long imageNo;

}
