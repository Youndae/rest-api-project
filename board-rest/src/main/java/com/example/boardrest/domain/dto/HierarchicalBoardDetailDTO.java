package com.example.boardrest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalBoardDetailDTO {

    private Long boardNo;

    private String boardTitle;

    private String nickname;

    private String boardContent;

    private Date boardDate;
}
