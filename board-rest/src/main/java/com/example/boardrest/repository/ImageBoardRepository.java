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

    /*@Query(value = "SELECT new com.example.boardrest.domain.dto.ImageDTO(" +
            "b.imageNo" +
            ", d.imageName" +
            ", b.imageContent" +
            ", b.imageDate" +
            ", b.imageTitle" +
            ", b.member.userId" +
            ", d.imageStep" +
            ", d.oldName) " +
            "FROM ImageBoard b " +
            "INNER JOIN ImageData d " +
            "ON b.imageNo = d.imageBoard.imageNo " +
            "GROUP BY b.imageNo " +
            "ORDER BY b.imageNo DESC")
    List<ImageDTO> imageBoardList();*/

    /*@Query(value = "SELECT new com.example.boardrest.domain.dto.ImageDTO(" +
            "b.imageNo" +
            ", d.imageName" +
            ", b.imageContent" +
            ", b.imageDate" +
            ", b.imageTitle" +
            ", b.member.userId" +
            ", d.imageStep" +
            ", d.oldName) " +
            "FROM ImageBoard b " +
            "INNER JOIN ImageData d " +
            "ON b.imageNo = d.imageBoard.imageNo " +
            "WHERE b.imageNo = ?1 " +
            "ORDER BY d.imageStep ASC")
    List<ImageDTO> imageDetailDTO(long imageNo);*/

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
            "ON ib.imageNo = id.imageBoard.imageNo"
    ,countQuery = "SELECT c.contentCount " +
            "FROM CountTable c " +
            "WHERE c.boardName = 'imageBoard'")
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
            "WHERE ib.imageTitle LIKE :keyword"
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
            "WHERE ib.imageContent LIKE :keyword"
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
            "WHERE ib.member.userId LIKE :keyword"
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
            "OR ib.imageContent LIKE :keyword"
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

    @Modifying
    @Transactional
    @Query(value = "UPDATE ImageBoard  b " +
            "SET b.imageTitle = ?1" +
            ", b.imageContent = ?2" +
            ", b.imageDataSet = ?3 " +
            "WHERE b.imageNo = ?4")
    void imageBoardModify(String imageTitle, String imageContent, Set<ImageData> imageDataSet, long imageNo);

}
