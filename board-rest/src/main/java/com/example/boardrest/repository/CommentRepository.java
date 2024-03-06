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

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query(value = "SELECT c.member.userId " +
                    "FROM Comment c " +
                    "WHERE c.commentNo = ?1")
    String findByUserId(long commentNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Comment c " +
            "SET c.commentStatus = 1 " +
            "WHERE c.commentNo = ?1")
    void deleteComment(long commentNo);


}
