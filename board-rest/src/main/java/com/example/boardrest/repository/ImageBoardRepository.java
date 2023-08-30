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

import java.util.Set;

public interface ImageBoardRepository extends JpaRepository<ImageBoard, Long> {

    //default List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardDTO(" +
            "ib.imageNo" +
            ", ib.imageTitle" +
            ", ib.member.userId" +
            ", ib.imageDate" +
            ", ib.imageContent" +
            ", id.imageName) " +
            "FROM ImageBoard ib " +
            "INNER JOIN " +
            "ImageData id " +
            "ON ib.imageNo = id.imageBoard.imageNo " +
            "GROUP BY ib.imageNo"
    ,countQuery = "SELECT count(distinct(ib.imageNo)) " +
            "FROM ImageBoard ib")
    Page<ImageBoardDTO> getImageBoardList(Pageable pageable);

    //searchTitle
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardDTO(" +
            "ib.imageNo" +
            ", ib.imageTitle" +
            ", ib.member.userId" +
            ", ib.imageDate" +
            ", ib.imageContent" +
            ", id.imageName) " +
            "FROM ImageBoard ib " +
            "INNER JOIN " +
            "ImageData id " +
            "ON ib.imageNo = id.imageBoard.imageNo " +
            "WHERE ib.imageTitle LIKE :keyword " +
            "GROUP BY ib.imageNo"
    ,countQuery = "SELECT count(ib) " +
            "FROM ImageBoard ib " +
            "WHERE ib.imageTitle LIKE :keyword")
    Page<ImageBoardDTO> getImageBoardSearchTitle(@Param("keyword") String keyword, Pageable pageable);

    //searchContent
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardDTO(" +
            "ib.imageNo" +
            ", ib.imageTitle" +
            ", ib.member.userId" +
            ", ib.imageDate" +
            ", ib.imageContent" +
            ", id.imageName) " +
            "FROM ImageBoard ib " +
            "INNER JOIN " +
            "ImageData id " +
            "ON ib.imageNo = id.imageBoard.imageNo " +
            "WHERE ib.imageContent LIKE :keyword " +
            "GROUP BY ib.imageNo"
    ,countQuery = "SELECT count(ib) " +
            "FROM ImageBoard ib " +
            "WHERE ib.imageContent LIKE :keyword")
    Page<ImageBoardDTO> getImageBoardSearchContent(@Param("keyword") String keyword, Pageable pageable);

    //searchWriter
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardDTO(" +
            "ib.imageNo" +
            ", ib.imageTitle" +
            ", ib.member.userId" +
            ", ib.imageDate" +
            ", ib.imageContent" +
            ", id.imageName) " +
            "FROM ImageBoard ib " +
            "INNER JOIN " +
            "ImageData id " +
            "ON ib.imageNo = id.imageBoard.imageNo " +
            "WHERE ib.member.userId LIKE :keyword " +
            "GROUP BY ib.imageNo"
    ,countQuery = "SELECT count(ib) " +
            "FROM ImageBoard ib " +
            "WHERE ib.member.userId LIKE :keyword")
    Page<ImageBoardDTO> getImageBoardSearchWriter(@Param("keyword") String keyword, Pageable pageable);

    //searchTitle & content
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardDTO(" +
            "ib.imageNo" +
            ", ib.imageTitle" +
            ", ib.member.userId" +
            ", ib.imageDate" +
            ", ib.imageContent" +
            ", id.imageName) " +
            "FROM ImageBoard ib " +
            "INNER JOIN " +
            "ImageData id " +
            "ON ib.imageNo = id.imageBoard.imageNo " +
            "WHERE ib.imageTitle LIKE :keyword " +
            "OR ib.imageContent LIKE :keyword " +
            "GROUP BY ib.imageNo"
    ,countQuery = "SELECT count(ib) " +
            "FROM ImageBoard ib " +
            "WHERE ib.imageTitle LIKE :keyword " +
            "OR ib.imageContent LIKE :keyword")
    Page<ImageBoardDTO> getImageBoardSearchTitleAndContent(@Param("keyword") String keyword, Pageable pageable);

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
