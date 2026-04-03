package com.example.boardrest.domain.dto.common.business;

import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.enumuration.ListAmount;
import lombok.Getter;

@Getter
public class PageCondition {
    private final int page;
    private final String keyword;
    private final String searchType;
    private final int amount;
    private final long offset;

    public PageCondition(Integer page, String keyword, String searchType, int amount) {
        this.page = page == null ? 1 : page;
        this.keyword = keyword == null ? null : "%" + keyword + "%";
        this.searchType = searchType;
        this.amount = amount;
        this.offset = (long) (this.page - 1) * amount;
    }

    public static PageCondition of(ListRequest request, ListAmount amount){
        return new PageCondition(
                request.getPage(),
                request.getKeyword(),
                request.getSearchType(),
                amount.getAmount()
        );
    }

    public static PageCondition of(int page, ListAmount amount) {
        return new PageCondition(
                page,
                null,
                null,
                amount.getAmount()
        );
    }
}
