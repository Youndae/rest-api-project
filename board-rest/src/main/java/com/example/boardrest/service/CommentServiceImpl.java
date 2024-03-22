package com.example.boardrest.service;

import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final PrincipalService principalService;

    private final CommentRepository commentRepository;

    // 댓글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long commentInsertProc(CommentInsertDTO dto, Principal principal) {

        Comment comment = Comment.builder()
                                .member(principalService.checkPrincipal(principal))
                                .commentContent(dto.getCommentContent())
                                .commentDate(Date.valueOf(LocalDate.now()))
                                .build();

        commentRepository.save(comment);
        comment.setCommentPatchData(dto);
        commentRepository.save(comment);

        return 1;
    }

    // 댓글 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public int commentDelete(long commentNo, Principal principal) {

        /**
         * DB에 저장된 데이터의 사용자 아이디를 가져와
         * 현재 로그인한 사용자의 아이디와 비교를 해 동일할 경우만 삭제 처리.
         */

        Comment comment = commentRepository
                .findById(commentNo)
                .orElseThrow(() -> new NullPointerException("NullPointerException"));

        String writer = comment.getMember().getUserId();

        if(writer.equals(principal.getName())){
            comment.setCommentStatus(1);
            commentRepository.save(comment);
            return 1;
        }else{
            return -1;
        }
    }

    // 댓글 List
    @Override
    public ResponsePageableListDTO<BoardCommentDTO> commentList(String boardNo, String imageNo, Criteria cri, Principal principal) {
        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getBoardAmount()
                , Sort.by("commentGroupNo").descending()
                        .and(Sort.by("commentUpperNo").ascending()));

        Page<BoardCommentDTO> comments = commentRepository.findAll(cri, pageable, boardNo, imageNo);

        ResponsePageableListDTO<BoardCommentDTO> responseDTO = new ResponsePageableListDTO<>(comments, principal);

//        return comments;

        return responseDTO;
    }
}












