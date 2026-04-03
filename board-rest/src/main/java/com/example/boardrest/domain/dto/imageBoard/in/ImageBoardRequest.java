package com.example.boardrest.domain.dto.imageBoard.in;

import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageBoardRequest {
    @Size(min = 2, message = "게시글 제목은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 제목은 2글자 이상이어야 합니다.")
    private String title;

    @Size(min = 2, message = "게시글 내용은 2글자 이상이어야 합니다.")
    @NotBlank(message = "게시글 내용은 2글자 이상이어야 합니다.")
    private String content;

    public ImageBoard toInsertEntity(Member member) {
        return ImageBoard.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .build();
    }
}
