package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

    int commentInsert(Map<String, Object> commentData, Comment comment);

    int commentReplyInsert(Map<String, Object> commentData, Comment comment);

    int commentDelete(Comment comment);

    List<Comment> commentList(Comment comment);
}
