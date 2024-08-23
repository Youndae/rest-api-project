package com.example.boardrest.domain.mapper;

import com.example.boardrest.domain.dto.paging.Criteria;
import org.springframework.stereotype.Component;

@Component
public class CriteriaRequestMapper {

    public static Criteria fromBoardRequest(int pageNum, String keyword, String searchType) {
        return Criteria.builder()
                .pageNum(pageNum)
                .keyword(keyword)
                .searchType(searchType)
                .build();
    }

    public static Criteria fromCommentRequest(int pageNum) {
        return Criteria.builder()
                        .pageNum(pageNum)
                        .build();
    }
}
