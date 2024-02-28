package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // HierarchicalBoard Comment List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.BoardCommentDTO(" +
            "c.commentNo" +
            ", c.member.userId" +
            ", c.commentDate" +
            ", CASE " +
            "WHEN (c.commentStatus > 0) THEN null " +
            "ELSE c.commentContent " +
            "END AS commentContent" +
            ", c.commentGroupNo" +
            ", c.commentIndent" +
            ", c.commentUpperNo) " +
            "FROM Comment c " +
            "WHERE c.hierarchicalBoard.boardNo = :boardNo"
            , countQuery = "SELECT count(c) " +
            "FROM Comment c " +
            "WHERE c.hierarchicalBoard.boardNo = :boardNo")
    Page<BoardCommentDTO> getHierarchicalBoardCommentList(Pageable pageable, @Param("boardNo") long boardNo);

    // ImageBoard Comment List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.BoardCommentDTO(" +
            "c.commentNo" +
            ", c.member.userId" +
            ", c.commentDate" +
            ", CASE " +
            "WHEN (c.commentStatus > 0) THEN null " +
            "ELSE c.commentContent " +
            "END AS commentContent" +
            ", c.commentGroupNo" +
            ", c.commentIndent" +
            ", c.commentUpperNo) " +
            "FROM Comment c " +
            "WHERE c.imageBoard.imageNo = :imageNo"
            , countQuery = "SELECT count(c) " +
            "FROM Comment c " +
            "WHERE c.imageBoard.imageNo = :imageNo")
    Page<BoardCommentDTO> getImageBoardCommentList(Pageable pageable, @Param("imageNo") long imageNo);


    @Modifying
    @Transactional
    @Query(value = "UPDATE comment " +
            "SET commentGroupNo = ?1" +
            ", commentIndent = ?2" +
            ", commentUpperNo = ?3" +
            ", boardNo = ?4" +
            ", imageNo = ?5 " +
            "WHERE commentNo = ?6"
            , nativeQuery = true)
    void patchComment(long commentGroupNo, long commentIndent, String commentUpperNo, Long boardNo, Long imageNo, long commentNo);


    @Query(value = "select userId " +
            "FROM comment " +
            "WHERE commentNo = ?1"
    , nativeQuery = true)
    String existsComment(long commentNo);


    @Modifying
    @Transactional
    @Query(value = "UPDATE comment " +
            "SET commentStatus = 1 " +
            "WHERE commentNo = ?1"
    , nativeQuery = true)
    void deleteComment(long commentNo);

}
