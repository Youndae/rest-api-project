package com.board.boardapp.dto;

import lombok.Data;

@Data
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

    public void setKeyword(String keyword) {
        this.keyword = keyword.equals("") ? null : keyword;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType.equals("") ? null : searchType;
    }
}
