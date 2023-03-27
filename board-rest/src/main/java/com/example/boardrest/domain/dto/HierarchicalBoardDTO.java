package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalBoardDTO {

    private Long boardNo;

    private String boardTitle;

    private String userId;

    private String boardContent;

    private Date boardDate;

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

}