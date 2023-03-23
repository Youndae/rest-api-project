package com.board.boardapp.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDataDTO {

    private String imageName;

    private String oldName;

    private int imageStep;
}
