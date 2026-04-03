package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardDetail;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardPatchDetail;
import com.example.boardrest.domain.dto.imageBoard.out.ImageBoardListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ImageBoardRepositoryCustom {

    Page<ImageBoardListResponse> findAllListByPageable(PageCondition condition, Pageable pageable);

    ImageBoardDetail findDetailById(long id);

    ImageBoardPatchDetail findPatchDetailById(long id);
}
