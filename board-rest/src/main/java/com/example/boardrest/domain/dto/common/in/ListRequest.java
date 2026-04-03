package com.example.boardrest.domain.dto.common.in;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListRequest {

    private Integer page;
    private String keyword;
    private String searchType;


    public void validate() {
        if((keyword == null) ^ (searchType == null))
            throw new IllegalArgumentException("Invalid RequestParam");
    }
}
