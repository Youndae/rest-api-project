package com.example.boardrest.domain.dto.hBoard.in;

import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import lombok.*;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardDTO {
//
    private String boardTitle;

    private String boardContent;

    public HierarchicalBoardReplyDTO toHierarchicalBoardReplyDTO() {
        return HierarchicalBoardReplyDTO.builder()
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .boardGroupNo(0L)
                .boardIndent(0)
                .boardUpperNo(null)
                .build();
    }
}