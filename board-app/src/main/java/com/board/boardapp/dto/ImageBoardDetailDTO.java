package com.board.boardapp.dto;

import lombok.*;

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

    private String userId;

    private Date imageDate;

    private String imageContent;

    private List<ImageDataDTO> imageData;

    private String uid;
}
