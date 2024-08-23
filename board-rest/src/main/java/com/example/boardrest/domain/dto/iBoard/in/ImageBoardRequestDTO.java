package com.example.boardrest.domain.dto.iBoard.in;

import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import lombok.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageBoardRequestDTO {
    private String imageTitle;

    private String imageContent;

    public ImageBoard toInsertEntity(Member member) {
        return ImageBoard.builder()
                .member(member)
                .imageTitle(this.imageTitle)
                .imageContent(this.imageContent)
                .build();
    }

    public ImageBoard toPatchEntity(Member member, ImageBoard imageBoard) {
        return ImageBoard.builder()
                .member(member)
                .imageNo(imageBoard.getImageNo())
                .imageTitle(this.imageTitle)
                .imageContent(this.imageContent)
                .imageDate(imageBoard.getImageDate())
                .build();
    }
}
