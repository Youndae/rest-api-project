package com.example.boardrest.domain.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDetailDataDTO {

    private String imageName;

    private String oldName;

    private int imageStep;
}
