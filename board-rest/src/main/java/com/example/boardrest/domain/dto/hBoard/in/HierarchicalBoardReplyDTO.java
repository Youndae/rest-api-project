package com.example.boardrest.domain.dto.hBoard.in;

import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchicalBoardReplyDTO {

    private String boardTitle;

    private String boardContent;

    private Long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

    public HierarchicalBoard toEntity(Member member) {
        return HierarchicalBoard.builder()
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .member(member)
                .build();
    }
}
