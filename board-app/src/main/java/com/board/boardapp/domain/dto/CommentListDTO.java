package com.board.boardapp.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class  CommentListDTO {

    @JsonProperty("content")
    private List<CommentDTO> boardContent;

    private int totalPages;

    private long totalElements;

//    private String uid;

    private PageDTO pageDTO;

    private UserStatusDTO userStatus;

    public void setPageDTO(PageDTO pageDTO) {
        this.pageDTO = pageDTO;
    }
}
