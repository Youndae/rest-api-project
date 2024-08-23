package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.comment.out.BoardCommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<BoardCommentDTO> findAll(Pageable pageable, String boardNo, String imageNo);
}
