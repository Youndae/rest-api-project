package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ImageDetailDTO {
//
    private long imageNo;

    private String imageTitle;

    private String userId;

    private Date imageDate;

    private String imageContent;
}
