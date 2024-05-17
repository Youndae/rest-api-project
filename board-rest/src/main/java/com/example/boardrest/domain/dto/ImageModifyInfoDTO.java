package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ImageModifyInfoDTO {
//
    private long imageNo;

    private String imageTitle;

    private String nickname;

    private Date imageDate;

    private String imageContent;
}
