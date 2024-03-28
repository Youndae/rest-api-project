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

    private boolean empty;
    private boolean first;
    private boolean last;
    private long number;
    private int totalPages;

    private PageDTO pageDTO;

    private UserStatusDTO userStatus;
}
