package com.board.boardapp.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaginationListDTO<T> {

    private List<T> content;

    private boolean empty;
    private boolean first;
    private boolean last;
    private long number;
    private int totalPages;

    private PageDTO pageDTO;

    private UserStatusDTO userStatus;

    public void setPageDTO(Criteria cri) {
        this.pageDTO = new PageDTO(cri, this.totalPages);
    }
}
