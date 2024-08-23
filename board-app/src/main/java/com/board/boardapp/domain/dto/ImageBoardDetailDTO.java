package com.board.boardapp.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageBoardDetailDTO {

    private long imageNo;

    private String imageTitle;

    private String nickname;

    private LocalDate imageDate;

    private String imageContent;

    private List<ImageDataDTO> imageData;

}
