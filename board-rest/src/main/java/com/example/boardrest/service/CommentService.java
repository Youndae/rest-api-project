package com.example.boardrest.service;

import com.example.boardrest.domain.dto.comment.out.BoardCommentResponse;
import com.example.boardrest.domain.dto.comment.in.CommentRequest;
import com.example.boardrest.domain.dto.response.PageResponse;

import java.security.Principal;

public interface CommentService {
    PageResponse<BoardCommentResponse> getBoardCommentList(long id, int page);

    PageResponse<BoardCommentResponse> getImageBoardCommentList(long id, int page);

    void insertBoardComment(long targetBoardId, CommentRequest request, String userId);

    void insertImageBoardComment(long targetBoardId, CommentRequest request, String userId);

    void deleteComment(long id, String userId);

    void insertReplyComment(long targetCommentId, CommentRequest request, String userId);
}
