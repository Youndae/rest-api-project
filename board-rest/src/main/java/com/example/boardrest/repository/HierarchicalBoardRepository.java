package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HierarchicalBoardRepository extends JpaRepository<HierarchicalBoard, Long>, HierarchicalBoardRepositoryCustom {

    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDetailDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.nickname" +
            ", b.boardContent" +
            ", b.boardDate) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardNo = ?1")
    HierarchicalBoardDetailDTO findBoardDetailByBoardNo(long boardNo);


    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardReplyInfoDTO(" +
            "b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard  b " +
            "WHERE b.boardNo = ?1")
    HierarchicalBoardReplyInfoDTO findReplyInfoByBoardNo(long boardNo);


    @Query(value = "SELECT b.member.userId " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardNo = ?1")
    String checkWriter(long boardNo);

    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.boardContent) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardNo = ?1")
    HierarchicalBoardModifyDTO getModifyData(long boardNo);


    @Modifying
    @Query(value = "DELETE FROM HierarchicalBoard h " +
            "WHERE h.boardGroupNo = ?1")
    void deleteByBoardGroupNo(long boardGroupNo);


    @Modifying
    @Query(value = "DELETE FROM HierarchicalBoard h WHERE h.boardNo in :delList")
    void deleteAllByBoardNoList(@Param("delList") List<Long> delList);

    @Query(value = "SELECT new com.example.boardrest.domain.dto.DeleteBoardDTO(" +
            "h.boardNo" +
            ", h.boardGroupNo" +
            ", h.boardIndent) " +
            "FROM HierarchicalBoard h " +
            "WHERE h.boardNo = ?1")
    DeleteBoardDTO getDeleteData(long boardNo);


    @Query(value = "SELECT new com.example.boardrest.domain.dto.DeleteGroupListDTO(" +
            "h.boardNo" +
            ", h.boardUpperNo) " +
            "FROM HierarchicalBoard h " +
            "WHERE h.boardGroupNo = ?1")
    List<DeleteGroupListDTO> getGroupList(long boardGroupNo);

}
