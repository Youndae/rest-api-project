package com.example.boardrest.domain.dto.iBoard.out;

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
public class ImageBoardDetailDTO {
//
    private long imageNo;

    private String imageTitle;

    private String nickname;

    private LocalDate imageDate;

    private String imageContent;

    private List<ImageDataDTO> imageData;

    public static ImageBoardDetailDTO fromEntity(ImageBoard imageBoard, List<ImageDataDTO> dataDTO) {
        return ImageBoardDetailDTO.builder()
                .imageNo(imageBoard.getImageNo())
                .imageTitle(imageBoard.getImageTitle())
                .nickname(imageBoard.getMember().getNickname())
                .imageContent(imageBoard.getImageContent())
                .imageDate(imageBoard.getImageDate())
                .imageData(dataDTO)
                .build();
    }
}
