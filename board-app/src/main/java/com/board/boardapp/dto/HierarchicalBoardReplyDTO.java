package com.board.boardapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalBoardReplyDTO {

    private Long boardNo;

    private String boardTitle;

    private String boardContent;

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;
}
