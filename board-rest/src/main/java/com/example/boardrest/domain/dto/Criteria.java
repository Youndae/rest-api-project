package com.example.boardrest.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class Criteria {

    private int pageNum;

    private final int boardAmount = 20;

    private final int imageAmount = 15;

    private String keyword;

    private String searchType;

    public Criteria(){
        this(1);
    }

    public Criteria(int pageNum){
        this.pageNum = pageNum;
    }

    public Criteria(int pageNum, String keyword, String searchType) {
        this.pageNum = pageNum;
        this.keyword = keyword == null ? null : "%" + keyword + "%";
        this.searchType = searchType;
    }


}
