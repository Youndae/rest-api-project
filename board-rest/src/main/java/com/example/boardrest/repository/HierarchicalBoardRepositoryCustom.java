package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface HierarchicalBoardRepositoryCustom {

    Page<HierarchicalBoardListDTO> findAll(Criteria cri, Pageable pageable);

}
