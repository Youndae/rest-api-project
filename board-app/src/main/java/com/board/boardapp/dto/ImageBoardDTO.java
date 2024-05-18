package com.board.boardapp.dto;

import lombok.*;

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

    private Date imageDate;

    private String imageContent;

    private String imageName;

}
