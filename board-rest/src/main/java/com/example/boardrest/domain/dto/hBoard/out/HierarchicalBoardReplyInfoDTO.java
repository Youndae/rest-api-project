package com.example.boardrest.domain.dto.hBoard.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HierarchicalBoardReplyInfoDTO {

    private long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

}
