package com.example.boardrest.domain.dto.iBoard.out;

import lombok.*;

import java.time.LocalDate;

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

    private LocalDate imageDate;

    private String imageName;
}
