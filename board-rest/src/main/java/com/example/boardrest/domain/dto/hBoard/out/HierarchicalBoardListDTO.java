package com.example.boardrest.domain.dto.hBoard.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardListDTO {
    //
    private long boardNo;

    private String boardTitle;

    private String nickname;

    private LocalDate boardDate;

    private int boardIndent;
}
