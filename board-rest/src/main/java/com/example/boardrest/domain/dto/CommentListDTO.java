package com.example.boardrest.domain.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListDTO {

    private Page<HierarchicalBoardCommentDTO> hierarchicalBoardCommentDTO;

    private Page<ImageBoardCommentDTO> imageBoardCommentDTO;
}
