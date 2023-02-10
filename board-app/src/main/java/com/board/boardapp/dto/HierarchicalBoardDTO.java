package com.board.boardapp.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
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