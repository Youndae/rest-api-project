package com.example.boardrest.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.json.JSONPropertyName;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCommentListDTO {

    @JsonProperty("content")
    private List<BoardCommentDTO> boardContent;

    private int totalPages;

    private long totalElements;

    private String uid;

    public void setUid(String uid) {
        this.uid = uid;
    }
}
