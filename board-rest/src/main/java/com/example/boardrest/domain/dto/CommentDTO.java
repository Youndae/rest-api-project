package com.example.boardrest.domain.dto;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Date;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private long commentNo;

    private String userId;

    private Date commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    private long boardNo;



}
