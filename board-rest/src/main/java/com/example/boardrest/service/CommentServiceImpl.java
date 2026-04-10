package com.example.boardrest.service;

import com.example.boardrest.customException.CustomNotFoundException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.domain.entity.Board;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.dto.comment.out.BoardCommentResponse;
import com.example.boardrest.domain.dto.comment.in.CommentRequest;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enums.ListAmount;
import com.example.boardrest.repository.BoardRepository;
import com.example.boardrest.repository.CommentRepository;
import com.example.boardrest.repository.ImageBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final PrincipalService principalService;

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final ImageBoardRepository imageBoardRepository;

    @Override
    public PageResponse<BoardCommentResponse> getBoardCommentList(long id, int page) {
        Pageable pageable = getCommentPageable(page);
        Page<BoardCommentResponse> content = commentRepository.findAllCommentByBoardId(id, pageable);

        return PageResponse.of(content);
    }
    @Override
    public PageResponse<BoardCommentResponse> getImageBoardCommentList(long id, int page) {
        Pageable pageable = getCommentPageable(page);
        Page<BoardCommentResponse> content = commentRepository.findAllCommentByImageBoardId(id, pageable);

        return PageResponse.of(content);
    }

    private Pageable getCommentPageable(int page) {
        PageCondition condition = PageCondition.of(page, ListAmount.COMMENT);

        return PageRequest.of(condition.getPage() - 1,
                condition.getAmount(),
                Sort.by("commentGroupNo").descending()
                        .and(Sort.by("commentUpperNo").ascending()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBoardComment(long targetBoardId, CommentRequest request, String userId) {
        Member memberEntity = principalService.getMemberByUserId(userId);
        Board targetBoard = boardRepository.findById(targetBoardId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "Insert comment target board is null"));

        Comment comment = request.toEntity(memberEntity, targetBoard);
        saveComment(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertImageBoardComment(long targetBoardId, CommentRequest request, String userId) {
        Member memberEntity = principalService.getMemberByUserId(userId);
        ImageBoard targetBoard = imageBoardRepository.findById(targetBoardId)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "Insert comment target board is null"));

        Comment comment = request.toEntity(memberEntity, targetBoard);
        saveComment(comment);
    }

    private void saveComment(Comment comment) {
        commentRepository.save(comment);
        comment.initializeRootPath();
    }

    // 댓글 delete
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(long id, String userId) {
        Comment comment = commentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new CustomNotFoundException(ErrorCode.BAD_REQUEST, "Delete target comment is null : " + id)
                            );

        principalService.validateUser(comment.getMember().getUserId(), userId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertReplyComment(long targetCommentId, CommentRequest request, String userId) {
        Comment targetComment = commentRepository.findNotDeleteCommentById(targetCommentId);

        if(targetComment == null) {
            log.warn("CommentService.insertReplyComment :: target comment is null. targetId={}, userId={}", targetCommentId, userId);
            throw new CustomNotFoundException(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage());
        }

        Member memberEntity = principalService.getMemberByUserId(userId);

        Comment comment = Comment.builder()
                .member(memberEntity)
                .board(targetComment.getBoard())
                .imageBoard(targetComment.getImageBoard())
                .content(request.getContent())
                .groupNo(targetComment.getGroupNo())
                .indent(targetComment.getIndent() + 1)
                .build();

        commentRepository.save(comment);
        comment.initializeReplyPath(targetComment.getUpperNo());
    }
}












