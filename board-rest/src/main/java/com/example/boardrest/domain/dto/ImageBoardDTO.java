package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageBoardDTO {
//
    private long imageNo;

    private String imageTitle;

    private String nickname;

    private Date imageDate;

    private String imageContent;

    private String imageName;
}
