package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.board.business.BoardPatchDetail;
import com.example.boardrest.domain.dto.board.out.BoardDetailResponse;
import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.board.out.BoardListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BoardRepositoryCustom {

    Page<BoardListResponse> findAllListByPageable(PageCondition condition, Pageable pageable);

    BoardDetailResponse findDetailResponseById(long id);

    BoardPatchDetail findPatchDetailById(long id);

    void deleteByPath(long groupNo, String selfUpper, String childUpper);
}
