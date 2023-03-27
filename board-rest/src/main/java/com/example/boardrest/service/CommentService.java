package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Criteria;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;

import java.security.Principal;

public interface CommentService {

    long commentInsert(CommentInsertDTO dto, Principal principal);

    long commentReplyInsert(CommentInsertDTO dto, Principal principal);

    int commentDelete(long commentNo, Principal principal);

    BoardCommentListDTO commentList(String boardNo, String imageNo, Criteria cri, Principal principal);
}
