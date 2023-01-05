package com.example.boardrest.repository;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

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

    /*@Query(value = "SELECT c.commentNo AS commentNo" +
            ", c.member.userId AS userId" +
            ", c.commentDate AS commentDate" +
            ", c.commentContent AS commentContent" +
            ", c.commentGroupNo AS commentGroupNo" +
            ", c.commentIndent AS commentIndent" +
            ", c.commentUpperNo AS commentUpperNo" +
            ", c.imageBoard.imageNo AS imageNo" +
            ", c.hierarchicalBoard.boardNo AS boardNo " +
            "FROM Comment c " +
            "WHERE boardNo = :boardNo"
    , countQuery = "SELECT count(c) " +
            "FROM Comment c " +
            "WHERE c.hierarchicalBoard.boardNo = :boardNo")
    Page<CommentDTO> getHierarchicalBoardCommentList(@Param("boardNo") long boardNo, Pageable pageable);*/

    @Query(value = "SELECT new com.example.boardrest.domain.dto.CommentDTO(" +
            "c.commentNo" +
            ", c.member.userId" +
            ", c.commentDate" +
            ", c.commentContent" +
            ", c.commentGroupNo" +
            ", c.commentIndent" +
            ", c.commentUpperNo" +
            ", c.hierarchicalBoard.boardNo) " +
            "FROM Comment c " +
            "WHERE c.hierarchicalBoard.boardNo = :boardNo"
    , countQuery = "SELECT count(c) " +
            "FROM Comment c " +
            "WHERE c.hierarchicalBoard.boardNo = :boardNo")
    Page<CommentDTO> getHierarchicalBoardCommentList(Pageable pageable, @Param("boardNo") long boardNo);

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
            "WHERE imageNo = :imageNo"
    , countQuery = "SELECT count(*) " +
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

    @Query(value = "SELECT count(c) FROM Comment c WHERE c.hierarchicalBoard.boardNo = ?1")
    long commentTotal(long boardNo);
}
