package com.example.boardrest.domain.dto.comment.in;

import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @Size(min = 2, message = "게시글 내용은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 내용은 2글자 이상이어야 합니다.")
    private String content;

    public Comment toEntity(Member memberEntity, Board boardEntity) {
        return Comment.builder()
                .member(memberEntity)
                .content(this.content)
                .indent(0)
                .imageBoard(null)
                .board(boardEntity)
                .build();
    }

    public Comment toEntity(Member memberEntity, ImageBoard imageBoardEntity) {
        return Comment.builder()
                .member(memberEntity)
                .content(this.content)
                .indent(0)
                .imageBoard(imageBoardEntity)
                .board(null)
                .build();
    }
}
