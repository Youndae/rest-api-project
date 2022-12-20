package com.example.boardrest.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Criteria {

    private int pageNum;

    private int amount;

    private String keyword;

    private String searchType;

    public Criteria(){
        this(1, 20);
    }

    public Criteria(int pageNum, int amount){
        this.pageNum = pageNum;
        this.amount = amount;
    }
}
