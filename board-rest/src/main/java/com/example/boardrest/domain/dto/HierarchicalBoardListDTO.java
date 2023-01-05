package com.example.boardrest.domain.dto;


import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalBoardListDTO {

    private List<HierarchicalBoardDTO> hierarchicalBoardDTOList;

    private PageDTO pageDTO;

}
