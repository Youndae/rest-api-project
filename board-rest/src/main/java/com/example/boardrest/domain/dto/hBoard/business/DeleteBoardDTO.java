package com.example.boardrest.domain.dto.hBoard.business;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DeleteBoardDTO {
//
    private long boardNo;

    private long boardGroupNo;

    private int boardIndent;
}
