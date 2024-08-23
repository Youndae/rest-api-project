package com.board.boardapp.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchicalBoardDTO {

    private Long boardNo;

    private String boardTitle;

    private String nickname;

    private String boardContent;

    private LocalDate boardDate;

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

    private UserStatusDTO userStats;
}