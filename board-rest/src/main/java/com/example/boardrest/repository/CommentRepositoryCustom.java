package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.comment.out.BoardCommentResponse;
import com.example.boardrest.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<BoardCommentResponse> findAllCommentByBoardId(long id, Pageable pageable);

    Page<BoardCommentResponse> findAllCommentByImageBoardId(long id, Pageable pageable);

    Comment findNotDeleteCommentById(long targetId);
}
