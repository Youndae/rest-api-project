package com.example.boardrest.domain.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentInsertDTO {
//
    private long commentNo;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    private Long boardNo;

    private Long imageNo;

}
