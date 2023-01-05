package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.CommentDTO;
import com.example.boardrest.domain.dto.CommentListDTO;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Map;

public interface CommentService {

    int commentInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentReplyInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentDelete(long commentNo);

    CommentListDTO commentList(Map<String, Object> boardNo, Criteria cri);
}
