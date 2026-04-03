package com.example.boardrest.domain.dto.imageBoard.out;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardListResponse {

    private long id;

    private String title;

    private String imageName;
}
