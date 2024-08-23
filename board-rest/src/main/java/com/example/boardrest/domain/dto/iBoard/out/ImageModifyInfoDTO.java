package com.example.boardrest.domain.dto.iBoard.out;

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

    private String imageContent;
}
