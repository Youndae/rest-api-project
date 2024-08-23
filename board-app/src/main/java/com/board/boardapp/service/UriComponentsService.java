package com.board.boardapp.service;

import com.board.boardapp.domain.dto.Criteria;
import org.springframework.web.util.UriComponentsBuilder;

public interface UriComponentsService {
    UriComponentsBuilder getListUri(String path, Criteria cri);
}
