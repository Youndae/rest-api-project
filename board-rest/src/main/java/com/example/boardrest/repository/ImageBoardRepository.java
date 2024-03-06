package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.entity.ImageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface ImageBoardRepository extends JpaRepository<ImageBoard, Long>, ImageBoardRepositoryCustom {

    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageDetailDTO(" +
            "b.imageNo" +
            ", b.imageTitle" +
            ", b.member.userId" +
            ", b.imageDate" +
            ", b.imageContent) " +
            "FROM ImageBoard b " +
            "WHERE b.imageNo = ?1 ")
    ImageDetailDTO imageDetailDTO(long imageNo);


}
