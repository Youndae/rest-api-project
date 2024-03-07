package com.board.boardapp.dto;

import lombok.Data;

@Data
public class Criteria {

    private int pageNum;

    private String keyword;

    private String searchType;

    public Criteria(){
        this(1);
    }

    public Criteria(int pageNum){
        this.pageNum = pageNum;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword.equals("") ? null : keyword;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType.equals("") ? null : searchType;
    }
}
