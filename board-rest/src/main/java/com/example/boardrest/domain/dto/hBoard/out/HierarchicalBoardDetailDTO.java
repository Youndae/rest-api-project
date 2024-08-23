package com.example.boardrest.domain.dto.hBoard.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalBoardDetailDTO {

    private Long boardNo;

    private String boardTitle;

    private String nickname;

    private String boardContent;

    private LocalDate boardDate;
}
