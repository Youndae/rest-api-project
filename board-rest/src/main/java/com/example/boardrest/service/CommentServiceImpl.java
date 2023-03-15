package com.example.boardrest.service;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentListDTO;
import com.example.boardrest.repository.CommentRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final PrincipalService principalService;

    private final CommentRepository commentRepository;

    // 댓글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public int commentInsert(Map<String, Object> commentData
                        , Comment comment
                        , Principal principal) {
        try{
            long commentNo = commentInsertGetCommentNo(commentData, principal);

            commentData.put("commentGroupNo", commentNo);
            commentData.put("commentIndent", 1);
            commentData.put("commentNo", commentNo);
            commentData.put("commentUpperNo", commentNo);

            checkBoard(commentData);

            return 1;
        }catch (Exception e){
            log.info("comment insertion failed");
            return -1;
        }

    }

    // 대댓글 insert
    @Override
    public int commentReplyInsert(Map<String, Object> commentData
                        , Comment comment
                        , Principal principal) {
        try{
            long commentNo = commentInsertGetCommentNo(commentData, principal);

            commentData.put("commentIndent", Integer.parseInt(commentData.get("commentIndent").toString()) + 1);
            commentData.put("commentUpperNo", commentData.get("commentUpperNo") + "," + commentNo);
            commentData.put("commentNo", commentNo);

            checkBoard(commentData);

            return 1;
        }catch (Exception e){
            log.info("comment reply insertion failed");
            return -1;
        }
    }

    // 댓글 delete
    @Override
    public int commentDelete(long commentNo) {
        try{
            commentRepository.deleteById(commentNo);
            return 1;
        }catch (Exception e){
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
    long commentInsertGetCommentNo(Map<String, Object> commentData, Principal principal) throws Exception{
        return commentRepository.save(
                Comment.builder()
                        .member(principalService.checkPrincipal(principal))
                        .commentContent(commentData.get("commentContent").toString())
                        .commentDate(Date.valueOf(LocalDate.now()))
                        .build()
        ).getCommentNo();
    }

    // 어느 게시판인지 체크(해당하는 게시판의 patch comment 호출)
    void checkBoard(Map<String, Object> commentData) throws Exception{

        if(commentData.get("boardNo") == null)
            patchImageComment(commentData);
        else if(commentData.get("boardNo") != null)
            patchHierarchicalComment(commentData);

    }

    // 이미지 게시판 comment patch
    void patchImageComment(Map<String, Object> commentData) throws Exception{
        log.info("patch imageBoard comment");

        commentRepository.patchImageComment(
                Long.parseLong(commentData.get("commentGroupNo").toString())
                , Integer.parseInt(commentData.get("commentIndent").toString())
                , commentData.get("commentUpperNo").toString()
                , Long.parseLong(commentData.get("imageNo").toString())
                , Long.parseLong(commentData.get("commentNo").toString())
        );
    }

    // 계층형 게시판 comment patch
    void patchHierarchicalComment(Map<String, Object> commentData) throws Exception{
        log.info("patch hierarchicalBoard comment");

        commentRepository.patchHierarchicalComment(
                Long.parseLong(commentData.get("commentGroupNo").toString())
                , Integer.parseInt(commentData.get("commentIndent").toString())
                , commentData.get("commentUpperNo").toString()
                , Long.parseLong(commentData.get("boardNo").toString())
                , Long.parseLong(commentData.get("commentNo").toString())
        );
    }



}
