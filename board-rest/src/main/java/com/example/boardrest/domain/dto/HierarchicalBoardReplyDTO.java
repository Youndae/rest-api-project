package com.example.boardrest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardReplyDTO {

    private Long boardNo;

    private String boardTitle;

    private String boardContent;

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;
}
