package com.example.boardrest.service;

import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
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
    public long commentInsert(CommentInsertDTO dto
                        , Principal principal) {


        log.info("commentInsertContent : {}, boardNo : {}, imageNo : {}", dto.getCommentContent(), dto.getBoardNo(), dto.getImageNo());
        try{
            long commentNo = commentInsertGetCommentNo(dto, principal);

            dto.setCommentNo(commentNo);
            dto.setCommentGroupNo(commentNo);
            dto.setCommentIndent(1);
            dto.setCommentUpperNo(String.valueOf(commentNo));

            checkBoard(dto);

            return 1;
        }catch (Exception e){
            log.info("comment insertion failed");
            return -1;
        }

    }

    // 대댓글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long commentReplyInsert(CommentInsertDTO dto
                        , Principal principal) {

        log.info("commentReply commentNo : {}, commentContent : {}, commentGroupNo : {}, commentIndent : {}, commentUpperNo : {}, boardNo : {}"
        , dto.getCommentNo()
        , dto.getCommentContent()
        , dto.getCommentGroupNo()
        , dto.getCommentIndent()
        , dto.getCommentUpperNo()
        , dto.getBoardNo());

        try{
            long commentNo = commentInsertGetCommentNo(dto, principal);

            dto.setCommentIndent(dto.getCommentIndent() + 1);
            dto.setCommentUpperNo(dto.getCommentUpperNo() + "," + commentNo);
            dto.setCommentNo(commentNo);

            checkBoard(dto);

            return 1;
        }catch (Exception e){
            log.info("comment reply insertion failed");
            return -1;
        }
    }

    // 댓글 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public int commentDelete(long commentNo, Principal principal) {

        /**
         * DB에 저장된 데이터의 사용자 아이디를 가져와
         * 현재 로그인한 사용자의 아이디와 비교를 해 동일할 경우만 삭제 처리.
         */
        String uidData = commentRepository.existsComment(commentNo);

        log.info("uidData : {}", uidData);

        if(commentRepository.existsComment(commentNo).equals(principal.getName())){
            try{
                commentRepository.deleteById(commentNo);
                return 1;
            }catch (Exception e){
                return -1;
            }
        }else{
            return -1;
        }


    }

    // 댓글 List
    @Override
    public BoardCommentListDTO commentList(String boardNo, String imageNo, Criteria cri, Principal principal) {

        /**
         * commentData에 넘어온 게시글 번호가
         * boardNo인지 ImageNo인지 체크 후
         * 그에 맞는 쿼리를 호출해 각 게시판 CommentDTO에 담아주고
         * 모든 commentDTO를 담고 있는 CommentListDTO에 builder 패턴으로 담아준 뒤 리턴.
         *
         */

        Page<BoardCommentDTO> hBoardDTO;

        if(boardNo == null){// imageBoard
            log.info("boardNo is null");
            // getImageBoardCommentList
            long iBoardNo = Long.parseLong(imageNo);

            hBoardDTO = commentRepository.getImageBoardCommentList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("commentGroupNo").descending()
                                    .and(Sort.by("commentUpperNo").ascending()))
                    , iBoardNo
            );

            /*dto = CommentListDTO.builder()
                    .hierarchicalBoardCommentDTO(null)
                    .imageBoardCommentDTO(commentDTO)
                    .build();*/

        }else if(boardNo != null){// hierarchicalBoard
            log.info("boardNo is not null");
            long hBoardNo = Long.parseLong(boardNo);

            hBoardDTO = commentRepository.getHierarchicalBoardCommentList(
                    PageRequest.of(cri.getPageNum() - 1
                            , cri.getAmount()
                            , Sort.by("commentGroupNo").descending()
                                    .and(Sort.by("commentUpperNo").ascending()))
                    , hBoardNo);

            /*dto = CommentListDTO.builder()
                    .hierarchicalBoardCommentDTO(commentDTO)
                    .imageBoardCommentDTO(null)
                    .build();*/

        }else{
            return null;
        }

        log.info("return dto : {}", hBoardDTO);

        String uid = null;

        if(principal != null)
            uid = principal.getName();

        /*BoardCommentListDTO result = BoardCommentListDTO.builder()
                    .boardComment(hBoardDTO)
                    .uid(uid)
                    .build();*/

        BoardCommentListDTO result;


        try{

            ObjectMapper om = new ObjectMapper();

            String boardDTOVal = om.writeValueAsString(hBoardDTO);

            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            result = om.readValue(boardDTOVal, BoardCommentListDTO.class);

            result.setUid(uid);

            log.info("result : {}", result);
        }catch (Exception e){
            log.info("fail");
            result = null;
        }

        log.info("return result : {}", result);

        return result;

    }

    // 1차 save commentNo 리턴
    long commentInsertGetCommentNo(CommentInsertDTO dto, Principal principal) throws Exception{
        return commentRepository.save(
                Comment.builder()
                        .member(principalService.checkPrincipal(principal))
                        .commentContent(dto.getCommentContent())
                        .commentDate(Date.valueOf(LocalDate.now()))
                        .build()
        ).getCommentNo();
    }

    // 어느 게시판인지 체크(해당하는 게시판의 patch comment 호출)
    void checkBoard(CommentInsertDTO dto) throws Exception{

        if(dto.getImageNo() != 0)
            patchImageComment(dto);
        else if(dto.getBoardNo() != 0)
            patchHierarchicalComment(dto);

    }

    // 이미지 게시판 comment patch
    void patchImageComment(CommentInsertDTO dto) throws Exception{
        log.info("patch imageBoard comment");

        commentRepository.patchImageComment(
                dto.getCommentGroupNo()
                , dto.getCommentIndent()
                , dto.getCommentUpperNo()
                , dto.getImageNo()
                , dto.getCommentNo()
        );
    }

    // 계층형 게시판 comment patch
    void patchHierarchicalComment(CommentInsertDTO dto) throws Exception{
        log.info("patch hierarchicalBoard comment");

        commentRepository.patchHierarchicalComment(
                dto.getCommentGroupNo()
                , dto.getCommentIndent()
                , dto.getCommentUpperNo()
                , dto.getBoardNo()
                , dto.getCommentNo()
        );
    }



}
