package com.example.boardrest.repository;

import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface HierarchicalBoardRepository extends JpaRepository<HierarchicalBoard, Long> {

    //HierarchicalBoard default List
    /*@Query(value = "SELECT boardNo" +
            ", CONCAT(REPEAT('    ', boardIndent), '', boardTitle) AS boardTitle" +
            ", userId" +
            ", boardContent" +
            ", boardDate" +
            ", boardGroupNo" +
            ", boardUpperNo" +
            ", boardIndent " +
            "FROM hierarchicalBoard"
    , countQuery = "SELECT count(*) " +
            "FROM hierarchicalBoard"
    , nativeQuery = true)
    Page<HierarchicalBoard> hierarchicalBoardList(Pageable pageable);*/

    /*//HierarchicalBoard SearchTitle List
    @Query(value = "SELECT boardNo" +
            ", CONCAT(REPEAT('   ', boardIndent), '', boardTitle) AS boardTitle" +
            ", userId" +
            ", boardContent" +
            ", boardDate" +
            ", boardGroupNo" +
            ", boardUpperNo" +
            ", boardIndent " +
            "FROM hierarchicalBoard " +
            "WHERE boardTitle LIKE :keyword"
            , countQuery = "SELECT count(*) " +
            "FROM hierarchicalBoard " +
            "WHERE boardTitle LIKE :keyword"
            , nativeQuery = true)
    Page<HierarchicalBoard> hierarchicalBoardListSearchTitle(@Param("keyword") String keyword, Pageable pageable);*/

    /*//HierarchicalBoard SearchContent List
    @Query(value = "SELECT boardNo" +
            ", CONCAT(REPEAT('   ', boardIndent), '', boardTitle) AS boardTitle" +
            ", userId" +
            ", boardContent" +
            ", boardDate" +
            ", boardGroupNo" +
            ", boardUpperNo" +
            ", boardIndent " +
            "FROM hierarchicalBoard " +
            "WHERE boardContent LIKE :keyword"
            , countQuery = "SELECT count(*) " +
            "FROM hierarchicalBoard " +
            "WHERE boardContent LIKE :keyword"
            , nativeQuery = true)
    Page<HierarchicalBoard> hierarchicalBoardListSearchContent(@Param("keyword") String keyword, Pageable pageable);*/

    /*//HierarchicalBoard SearchUser List
    @Query(value = "SELECT boardNo" +
            ", CONCAT(REPEAT('   ', boardIndent), '', boardTitle) AS boardTitle" +
            ", userId" +
            ", boardContent" +
            ", boardDate" +
            ", boardGroupNo" +
            ", boardUpperNo" +
            ", boardIndent " +
            "FROM hierarchicalBoard " +
            "WHERE userId LIKE :keyword"
            , countQuery = "SELECT count(*) " +
            "FROM hierarchicalBoard " +
            "WHERE userId LIKE :keyword"
            , nativeQuery = true)
    Page<HierarchicalBoard> hierarchicalBoardListSearchUser(@Param("keyword") String keyword, Pageable pageable);*/

    /*//HierarchicalBoard Search Title + Content List
    @Query(value = "SELECT boardNo" +
            ", CONCAT(REPEAT('   ', boardIndent), '', boardTitle) AS boardTitle" +
            ", userId" +
            ", boardContent" +
            ", boardDate" +
            ", boardGroupNo" +
            ", boardUpperNo" +
            ", boardIndent " +
            "FROM hierarchicalBoard " +
            "WHERE boardTitle LIKE :keyword " +
            "OR boardContent LIKE :keyword"
            , countQuery = "SELECT count(*) " +
            "FROM hierarchicalBoard " +
            "WHERE boardTitle LIKE :keyword " +
            "OR boardContent LIKE :keyword"
            , nativeQuery = true)
    Page<HierarchicalBoard> hierarchicalBoardListSearchTitleOrContent(@Param("keyword") String keyword, Pageable pageable);*/

    //HierarchicalBoard default List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b"
    , countQuery = "SELECT count(b) " +
            "FROM HierarchicalBoard b")
    Page<HierarchicalBoardDTO> hierarchicalBoardList(Pageable pageable);


    /*//HierarchicalBoard searchTitle List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword"
            , countQuery = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword")
    Page<HierarchicalBoardDTO> hierarchicalBoardListSearchTitle(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard searchContent List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardContent LIKE :keyword"
            , countQuery = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardContent LIKE :keyword")
    Page<HierarchicalBoardDTO> hierarchicalBoardListSearchContent(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard searchUser List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.member.userId LIKE :keyword"
            , countQuery = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.member.userId LIKE :keyword")
    Page<HierarchicalBoardDTO> hierarchicalBoardListSearchUser(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard searchTitle + content List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword " +
            "OR b.boardContent LIKE :keyword"
    , countQuery = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword " +
            "OR b.boardContent LIKE :keyword")
    Page<HierarchicalBoardDTO> hierarchicalBoardListSearchTitleOrContent(@Param("keyword") String keyword, Pageable pageable);*/


    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardNo = ?1"
    , nativeQuery = true)
    HierarchicalBoardDTO findByBoardNo(long boardNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hierarchicalBoard " +
            "SET boardTitle = :boardTitle" +
            ", boardContent = :boardContent " +
            "WHERE boardNo = :boardNo",
            nativeQuery = true)
    HierarchicalBoard boardModify(@Param("boardTitle") String boardTitle, @Param("boardContent") String boardContent, @Param("boardNo") long boardNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hierarchicalBoard " +
            "SET boardContent = ?1" +
            ", boardIndent = ?2" +
            ", boardGroupNo = ?3" +
            ", boardUpperNo = ?4 " +
            "WHERE boardNo = ?5"
    , nativeQuery = true)
    HierarchicalBoard boardInsertPatch(String boardContent, int boardIndent, long boardGroupNo, String boardUpperNo, long boardNo);


    /*@Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardDTO(" +
            "b.boardNo" +
            ", b.boardTitle" +
            ", b.member.userId" +
            ", b.boardContent" +
            ", b.boardDate" +
            ", b.boardGroupNo" +
            ", b.boardIndent" +
            ", b.boardUpperNo) " +
            "FROM HierarchicalBoard b"
    , countQuery = "SELECT count(h)" +
            "FROM HierarchicalBoard h")
    Page<HierarchicalBoardDTO> boardListTest(Pageable pageable);*/
}
