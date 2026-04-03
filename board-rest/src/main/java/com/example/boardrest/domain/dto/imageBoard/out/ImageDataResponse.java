package com.example.boardrest.domain.dto.imageBoard.out;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDataResponse {
    private String imageName;

    private String originName;

    private int imageStep;
}
