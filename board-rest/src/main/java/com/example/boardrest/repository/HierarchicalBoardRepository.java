package com.example.boardrest.repository;

import com.example.boardrest.domain.HierarchicalBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface HierarchicalBoardRepository extends JpaRepository<HierarchicalBoard, Long> {

    //HierarchicalBoard default List
    @Query(value = "SELECT boardNo" +
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
    Page<HierarchicalBoard> hierarchicalBoardList(Pageable pageable);

    //HierarchicalBoard SearchTitle List
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
    Page<HierarchicalBoard> hierarchicalBoardListSearchTitle(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard SearchContent List
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
    Page<HierarchicalBoard> hierarchicalBoardListSearchContent(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard SearchUser List
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
    Page<HierarchicalBoard> hierarchicalBoardListSearchUser(@Param("keyword") String keyword, Pageable pageable);

    //HierarchicalBoard Search Title + Content List
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
    Page<HierarchicalBoard> hierarchicalBoardListSearchTitleOrContent(@Param("keyword") String keyword, Pageable pageable);


    HierarchicalBoard findByBoardNo(long boardNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE hierarchicalBoard " +
            "SET boardTitle = :boardTitle" +
            ", boardContent = :boardContent " +
            "WHERE boardNo = :boardNo",
            nativeQuery = true)
    void boardModify(@Param("boardTitle") String boardTitle, @Param("boardContent") String boardContent, @Param("boardNo") long boardNo);


    @Query(value = "SELECT ifnull(max(boardNo) + 1, 1) " +
            "FROM hierarchicalBoard",
            nativeQuery = true)
    long maxBoardNo();
}
