package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

}
