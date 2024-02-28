package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;


@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalBoardDTO {

    private Long boardNo;

    private String boardTitle;

    private String userId;

    private String boardContent;

    private Date boardDate;

    private long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

    public void setBoardGroupNo(long boardGroupNo) {
        if(this.boardGroupNo == 0)
            this.boardGroupNo = boardGroupNo;
    }

    public void setBoardUpperNo(String boardUpperNo) {
        this.boardUpperNo = this.boardUpperNo == null ? boardUpperNo : this.boardUpperNo + "," + boardUpperNo;
    }

}