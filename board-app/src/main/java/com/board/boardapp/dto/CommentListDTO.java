package com.board.boardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CommentListDTO {

    @JsonProperty("content")
    private List<CommentDTO> boardContent;

    private int totalPages;

    private long totalElements;

//    private String uid;

    private PageDTO pageDTO;

    public void setPageDTO(PageDTO pageDTO) {
        this.pageDTO = pageDTO;
    }
}
