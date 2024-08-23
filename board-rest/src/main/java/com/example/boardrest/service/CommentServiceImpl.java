package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.comment.out.BoardCommentDTO;
import com.example.boardrest.domain.dto.comment.in.CommentInsertDTO;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.Result;
import com.example.boardrest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final PrincipalService principalService;

    private final CommentRepository commentRepository;

    // 댓글 List
    @Override
    public Page<BoardCommentDTO> commentList(String boardNo, String imageNo, Criteria cri) {
        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getBoardAmount()
                , Sort.by("commentGroupNo").descending()
                        .and(Sort.by("commentUpperNo").ascending()));

        return commentRepository.findAll(pageable, boardNo, imageNo);
    }

    // 댓글 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String commentInsertProc(CommentInsertDTO dto, Principal principal) {

        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();
        Comment comment = dto.toEntity(memberEntity);

        commentRepository.save(comment);
        comment.setCommentPatchData(dto);
        commentRepository.save(comment);

        return Result.SUCCESS.getResultMessage();
    }

    // 댓글 delete
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String commentDelete(long commentNo, Principal principal) {

        /**
         * DB에 저장된 데이터의 사용자 아이디를 가져와
         * 현재 로그인한 사용자의 아이디와 비교를 해 동일할 경우만 삭제 처리.
         */

        Comment comment = commentRepository
                            .findById(commentNo)
                            .orElseThrow(() ->
                                    new IllegalArgumentException("invalid commentNo : " + commentNo)
                            );

        principalService.validateUser(comment, principal);
        comment.setCommentStatus(1);
        commentRepository.save(comment);

        return Result.SUCCESS.getResultMessage();
    }
}












