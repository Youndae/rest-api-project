package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BoardCommentDTO {

    private long commentNo;

    private String userId;

    private Date commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

}
