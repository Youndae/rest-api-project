package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<BoardCommentDTO> findAll(Pageable pageable, String boardNo, String imageNo);
}
