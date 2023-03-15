package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Map;

public interface CommentService {

    int commentInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentReplyInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentDelete(long commentNo);

    BoardCommentListDTO commentList(String boardNo, String imageNo, Criteria cri, Principal principal);
}
