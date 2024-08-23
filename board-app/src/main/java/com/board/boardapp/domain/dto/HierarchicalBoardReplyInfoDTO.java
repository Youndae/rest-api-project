package com.board.boardapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardReplyInfoDTO {

    private Long boardNo;

    private long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;
}
