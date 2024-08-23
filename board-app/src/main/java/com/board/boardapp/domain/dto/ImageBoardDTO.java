package com.board.boardapp.domain.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardDTO {

    private long imageNo;

    private String imageTitle;

    private String nickname;

    private LocalDate imageDate;

    private String imageContent;

    private String imageName;

}
