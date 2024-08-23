package com.board.boardapp.domain.dto.hBoard.in;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardInsertDTO {

    private String boardTitle;

    private String boardContent;
}
