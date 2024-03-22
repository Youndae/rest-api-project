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

    private boolean empty;
    private boolean first;
    private boolean last;
    private long number;
    private int totalPages;

    private PageDTO pageDTO;

    private UserStatusDTO userStatus;
}
