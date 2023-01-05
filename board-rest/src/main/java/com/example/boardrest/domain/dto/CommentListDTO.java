package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListDTO {

    private List<CommentDTO> commentDTOList;

    private PageDTO pageDTO;
}
