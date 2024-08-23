package com.example.boardrest.service;

import com.example.boardrest.domain.dto.comment.out.BoardCommentDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.comment.in.CommentInsertDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;

public interface CommentService {


    Page<BoardCommentDTO> commentList(String boardNo, String imageNo, Criteria cri);

    String commentInsertProc(CommentInsertDTO dto, Principal principal);

    String commentDelete(long commentNo, Principal principal);
}
