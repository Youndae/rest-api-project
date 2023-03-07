package com.board.boardapp.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalBoardDetailDTO {

    private HierarchicalBoardDTO detailData;

    private String uid;
}
