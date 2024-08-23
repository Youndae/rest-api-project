package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ImageBoardRepositoryCustom {

    Page<ImageBoardDTO> findAll(Criteria cri, Pageable pageable);
}
