package com.example.boardrest.domain.dto.board.in;

import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {

    @Size(min = 2, message = "게시글 제목은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 제목은 2글자 이상이어야 합니다.")
    private String title;

    @Size(min = 2, message = "게시글 내용은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 내용은 2글자 이상이어야 합니다.")
    private String content;

    public Board toEntity(Member memberEntity) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .member(memberEntity)
                .indent(0)
                .build();
    }
}