package com.board.boardapp.domain.dto.iBoard.in;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardInsertDTO {

    private String imageTitle;

    private String imageContent;
}
