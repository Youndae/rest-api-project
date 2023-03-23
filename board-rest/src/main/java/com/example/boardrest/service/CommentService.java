package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Map;

public interface CommentService {

    long commentInsert(CommentInsertDTO dto, Principal principal);

    long commentReplyInsert(CommentInsertDTO dto, Principal principal);

    int commentDelete(long commentNo, Principal principal);

    BoardCommentListDTO commentList(String boardNo, String imageNo, Criteria cri, Principal principal);
}
