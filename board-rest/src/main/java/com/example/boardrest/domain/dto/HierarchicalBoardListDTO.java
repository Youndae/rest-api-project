package com.example.boardrest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardListDTO {
    //
    private long boardNo;

    private String boardTitle;

    private String nickname;

    private Date boardDate;

    private int boardIndent;
}
