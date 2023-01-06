package com.example.boardrest.repository;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.dto.HierarchicalBoardCommentDTO;
import com.example.boardrest.domain.dto.ImageBoardCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // HierarchicalBoard Comment List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.HierarchicalBoardCommentDTO(" +
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
    Page<HierarchicalBoardCommentDTO> getHierarchicalBoardCommentList(Pageable pageable, @Param("boardNo") long boardNo);

    // ImageBoard Comment List
    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageBoardCommentDTO(" +
            "c.commentNo" +
            ", c.member.userId" +
            ", c.commentDate" +
            ", c.commentContent" +
            ", c.commentGroupNo" +
            ", c.commentIndent" +
            ", c.commentUpperNo" +
            ", c.imageBoard.imageNo) " +
            "FROM Comment c " +
            "WHERE c.imageBoard.imageNo = :imageNo"
    , countQuery = "SELECT count(c) " +
            "FROM Comment c " +
            "WHERE c.imageBoard.imageNo = :imageNo")
    Page<ImageBoardCommentDTO> getImageBoardCommentList(Pageable pageable, @Param("imageNo") long imageNo);

    // patch after saving ImageBoard comments
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

    // Patch after saving HierarchicalBoard comments
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
