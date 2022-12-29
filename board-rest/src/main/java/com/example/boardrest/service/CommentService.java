package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface CommentService {

    int commentInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentReplyInsert(Map<String, Object> commentData, Comment comment, Principal principal);

    int commentDelete(long commentNo);

    List<Comment> commentList(Map<String, Object> boardNo);
}
