package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.repository.CommentRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

        System.out.println("dto.getIndent : " + dto.getCommentIndent());

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

        if(commentRepository.existsComment(commentNo).equals(principal.getName())){
            commentRepository.deleteComment(commentNo);
            return 1;
        }else{
            return -1;
        }
    }

    // 댓글 List
    @Override
    public BoardCommentListDTO commentList(String boardNo, String imageNo, Criteria cri, Principal principal) {

        Page<BoardCommentDTO> hBoardDTO;

        if(boardNo == null){// imageBoard
            // getImageBoardCommentList
            long iBoardNo = Long.parseLong(imageNo);

            hBoardDTO = commentRepository.getImageBoardCommentList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("commentGroupNo").descending()
                                    .and(Sort.by("commentUpperNo").ascending()))
                    , iBoardNo
            );
        }else if(boardNo != null){// hierarchicalBoard
            long hBoardNo = Long.parseLong(boardNo);

            hBoardDTO = commentRepository.getHierarchicalBoardCommentList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getBoardAmount()
                            , Sort.by("commentGroupNo").descending()
                                    .and(Sort.by("commentUpperNo").ascending()))
                    , hBoardNo);
        }else{
            return null;
        }

        BoardCommentListDTO result;

        try{

            ObjectMapper om = new ObjectMapper();
            String boardDTOVal = om.writeValueAsString(hBoardDTO);

            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            result = om.readValue(boardDTOVal, BoardCommentListDTO.class);
        }catch (Exception e){
            result = null;
        }

        return result;
    }
}