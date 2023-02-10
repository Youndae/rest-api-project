package com.board.boardapp.dto;


import lombok.*;

import java.util.List;

@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalBoardListDTO {

    private List<HierarchicalBoardDTO> content;

    /*private boolean last;

    private long totalPages;

    private long totalElements;

    private long number;

    private int size;

    private int numberOfElements;

    private boolean first;

    private boolean empty;*/

    private PageDTO pageDTO;

}
