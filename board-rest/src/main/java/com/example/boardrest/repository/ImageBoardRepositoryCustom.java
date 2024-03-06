package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.ImageBoardDTO;
import com.example.boardrest.domain.entity.ImageBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ImageBoardRepositoryCustom {

    Page<ImageBoardDTO> findAll(Criteria cri, Pageable pageable);
}
