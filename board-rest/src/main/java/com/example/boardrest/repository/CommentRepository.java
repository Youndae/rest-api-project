package com.example.boardrest.repository;

import com.example.boardrest.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT commentNo" +
            ", CONCAT(REPEAT('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', commentIndent), '', commentContent) AS commentContent" +
            ", userId" +
            ", commentDate" +
            ", commentGroupNo" +
            ", commentIndent" +
            ", commentUpperNo" +
            ", imageNo" +
            ", boardNo " +
            "FROM comment " +
            "WHERE boardNo = :boardNo",
    countQuery = "SELECT count(*) " +
            "FROM comment " +
            "WHERE boardNo = :boardNo",
    nativeQuery = true)
    Page<Comment> hierarchicalCommentList(@Param("boardNo") long boardNo, Pageable pageable);

    @Query(value = "SELECT commentNo" +
            ", CONCAT(REPEAT('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', commentIndent), '', commentContent) AS commentContent" +
            ", userId" +
            ", commentDate" +
            ", commentGroupNo" +
            ", commentIndent" +
            ", commentUpperNo" +
            ", imageNo" +
            ", boardNo " +
            "FROM comment " +
            "WHERE imageNo = :imageNo",
    countQuery = "SELECT count(*) " +
            "FROM comment " +
            "WHERE imageNo = :imageNo",
    nativeQuery = true)
    Page<Comment> imageCommentList(@Param("imageNo") long imageNo, Pageable pageable);

    @Query(value = "SELECT count(*) " +
            "FROM comment " +
            "WHERE boardNo = :boardNo"
    , nativeQuery = true)
    int countBoardComment(@Param("boardNo") long boardNo);

    @Query(value = "SELECT count(*) " +
            "FROM comment " +
            "WHERE imageNo = :imageNo"
    , nativeQuery = true)
    int countImageComment(@Param("imageNo") long imageNo);

    @Query(value = "SELECT ifnull(max(commentNo) + 1, 1) " +
            "FROM comment"
    , nativeQuery = true)
    long maxCommentNo();

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment " +
            "SET commentGroupNo = ?1" +
            ", commentIndent = ?2" +
            ", commentUpperNo = ?3" +
            ", imageNo = ?4 " +
            "WHERE commentNo = ?5"
    , nativeQuery = true)
    void patchImageComment(long commentGroupNo, int commentIndent, String commentUpperNo, long imageNo, long commentNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment " +
            "SET commentGroupNo = ?1" +
            ", commentIndent = ?2" +
            ", commentUpperNo = ?3" +
            ", boardNo = ?4 " +
            "WHERE commentNo = ?5"
            , nativeQuery = true)
    void patchHierarchicalComment(long commentGroupNo, int commentIndent, String commentUpperNo, long boardNo, long commentNo);
}
