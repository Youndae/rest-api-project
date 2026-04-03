package com.example.boardrest.domain.dto.board.in;

import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardReplyRequest {

    @Size(min = 2, message = "게시글 제목은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 제목은 2글자 이상이어야 합니다.")
    private String title;

    @Size(min = 2, message = "게시글 내용은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 내용은 2글자 이상이어야 합니다.")
    private String content;

    public Board toEntity(Member memberEntity, Board targetBoard) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .member(memberEntity)
                .groupNo(targetBoard.getGroupNo())
                .indent(targetBoard.getIndent() + 1)
                .build();
    }
}
