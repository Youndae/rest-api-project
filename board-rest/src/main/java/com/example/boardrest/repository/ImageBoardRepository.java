package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.iBoard.out.ImageModifyInfoDTO;
import com.example.boardrest.domain.entity.ImageBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageBoardRepository extends JpaRepository<ImageBoard, Long>, ImageBoardRepositoryCustom {

    @Query(value = "SELECT new com.example.boardrest.domain.dto.iBoard.out.ImageModifyInfoDTO(" +
            "b.imageNo" +
            ", b.imageTitle" +
            ", b.imageContent) " +
            "FROM ImageBoard b " +
            "WHERE b.imageNo = ?1 ")
    ImageModifyInfoDTO findPatchDetail(long imageNo);


}
