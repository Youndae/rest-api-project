package com.board.boardapp.dto;

import lombok.*;

@Getter
@ToString
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    private int startPage;

    private int endPage;

    private boolean prev, next;

    private Criteria cri;

    public PageDTO(Criteria cri, int totalPages){
        this.cri = cri;

        this.endPage = (int) (Math.ceil(cri.getPageNum() / 10.0) * 10);
        this.startPage = this.endPage - 9;

        int realEnd = totalPages;

        if(realEnd < this.endPage) this.endPage = realEnd;

        this.prev = this.startPage > 1;
        this.next = this.endPage < realEnd;

    }


}
