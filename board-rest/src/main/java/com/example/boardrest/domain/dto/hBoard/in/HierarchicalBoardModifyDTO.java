package com.example.boardrest.domain.dto.hBoard.in;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalBoardModifyDTO {
//
    private Long boardNo;

    private String boardTitle;

    private String boardContent;
}
