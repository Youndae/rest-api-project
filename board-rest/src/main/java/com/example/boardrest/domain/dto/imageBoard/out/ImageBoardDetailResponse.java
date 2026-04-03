package com.example.boardrest.domain.dto.imageBoard.out;

import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardDetail;
import com.example.boardrest.domain.entity.ImageBoard;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageBoardDetailResponse {
    private String title;
    private String content;
    private String writer;
    private String writerId;
    private LocalDate createdAt;
    private List<String> imageDataList;

    public static ImageBoardDetailResponse of(ImageBoardDetail detail, List<String> imageData) {
        return ImageBoardDetailResponse.builder()
                .title(detail.getTitle())
                .content(detail.getContent())
                .writer(detail.getWriter())
                .writerId(detail.getWriterId())
                .createdAt(detail.getCreatedAt().toLocalDate())
                .imageDataList(imageData)
                .build();
    }
}
