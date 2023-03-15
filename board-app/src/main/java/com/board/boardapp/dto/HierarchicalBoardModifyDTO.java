package com.board.boardapp.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalBoardModifyDTO {

    private Long boardNo;

    private String boardTitle;

    private String boardContent;

}
