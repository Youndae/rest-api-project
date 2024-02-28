package com.example.boardrest.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class Criteria {

    private int pageNum;

    private int boardAmount;

    private int imageAmount;

    private String keyword;

    private String searchType;

    public Criteria(){
        this(1, 20, 15);
    }

    public Criteria(int pageNum, int boardAmount, int imageAmount){
        this.pageNum = pageNum;
        this.boardAmount = boardAmount;
        this.imageAmount = imageAmount;
    }

    public Criteria(int pageNum, int boardAmount, int imageAmount, String keyword, String searchType) {
        this.pageNum = pageNum;
        this.boardAmount = boardAmount;
        this.imageAmount = imageAmount;
        this.keyword = keyword == null ? null : "%" + keyword + "%";
        this.searchType = searchType;
    }
}
