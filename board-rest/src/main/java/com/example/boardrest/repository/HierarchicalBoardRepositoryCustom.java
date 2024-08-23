package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface HierarchicalBoardRepositoryCustom {

    Page<HierarchicalBoardListDTO> findAll(Criteria cri, Pageable pageable);

}
