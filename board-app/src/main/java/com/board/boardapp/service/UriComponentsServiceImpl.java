package com.board.boardapp.service;

import com.board.boardapp.domain.dto.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UriComponentsServiceImpl implements UriComponentsService{

    @Override
    public UriComponentsBuilder getListUri(String path, Criteria cri) {
        UriComponentsBuilder ub = UriComponentsBuilder.newInstance()
                .path(path)
                .queryParam("pageNum", cri.getPageNum());

        if(cri.getKeyword() != null)
            ub.queryParam("keyword", cri.getKeyword())
                    .queryParam("searchType", cri.getSearchType());

        return ub;
    }
}
