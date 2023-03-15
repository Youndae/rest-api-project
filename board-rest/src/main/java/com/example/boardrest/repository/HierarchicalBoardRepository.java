package com.example.boardrest.repository;

import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardReplyDTO;
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
    , countQuery = "SELECT c.contentCount " +
            "FROM Count_table c " +
            "WHERE c.boardName = 'hierarchicalboard'")
    Page<HierarchicalBoardDTO> hierarchicalBoardList(Pageable pageable);


    //HierarchicalBoard searchTitle List
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
    Page<HierarchicalBoardDTO> hierarchicalBoardListSearchTitleOrContent(@Param("keyword") String keyword, Pageable pageable);


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
            "WHERE b.boardNo = ?1")
    HierarchicalBoardDTO findByBoardNo(long boardNo);


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
    @Transactional
    @Query(value = "UPDATE hierarchicalBoard " +
            "SET boardTitle = :boardTitle" +
            ", boardContent = :boardContent " +
            "WHERE boardNo = :boardNo",
            nativeQuery = true)
    void boardModify(@Param("boardTitle") String boardTitle, @Param("boardContent") String boardContent, @Param("boardNo") long boardNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hierarchicalBoard " +
            "SET boardIndent = ?1" +
            ", boardGroupNo = ?2" +
            ", boardUpperNo = ?3 " +
            "WHERE boardNo = ?4"
    , nativeQuery = true)
    void boardInsertPatch(int boardIndent, long boardGroupNo, String boardUpperNo, long boardNo);

    @Query(value = "SELECT " +
            "b.boardGroupNo AS boardGroupNo" +
            ", b.boardIndent AS boardIndent" +
            ", b.boardUpperNo AS boardUpperNo " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardNo = ?1")
    HierarchicalBoardReplyDTO getReplyData(long boardNo);


    // 기본 boardList TotalCount
    @Query(value = "SELECT c.contentCount " +
            "FROM Count_table c " +
            "WHERE c.boardName = 'hierarchicalBoard'")
    long defaultBoardTotalCount();

    // searchTitle TotalCount
    @Query(value = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword")
    long searchTitleTotalCount(@Param("keyword") String keyword);

    // searchContent TotalCount
    @Query(value = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardContent LIKE :keyword")
    long searchContentTotalCount(@Param("keyword") String keyword);

    // searchUser TotalCount
    @Query(value = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.member.userId LIKE :keyword")
    long searchUserTotalCount(@Param("keyword") String keyword);

    // searchTitle + content TotalCount
    @Query(value = "SELECT count(b) " +
            "FROM HierarchicalBoard b " +
            "WHERE b.boardTitle LIKE :keyword " +
            "OR b.boardContent LIKE :keyword")
    long searchTitleOrContentTotalCount(@Param("keyword") String keyword);
}
