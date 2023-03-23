package com.board.boardapp.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ImageBoardListDTO {

    private List<ImageBoardDTO> content;

    private int totalPages;

    private long totalElements;

    private PageDTO pageDTO;
}
