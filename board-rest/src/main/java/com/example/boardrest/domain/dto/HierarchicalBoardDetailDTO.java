package com.example.boardrest.domain.dto;

import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardDetailDTO {

    private HierarchicalBoardDTO detailData;

    private String uid;

}
