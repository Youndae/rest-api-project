package com.board.boardapp.domain.dto.hBoard.in;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardReplyInsertDTO extends HierarchicalBoardInsertDTO{

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;
}
