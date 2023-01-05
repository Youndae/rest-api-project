package com.example.boardrest.controller;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.Member;
import com.example.boardrest.domain.dto.CommentDTO;
import com.example.boardrest.domain.dto.CommentListDTO;
import com.example.boardrest.repository.CommentRepository;
import com.example.boardrest.service.CommentService;
import com.example.boardrest.service.PrincipalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/commnet")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    private PrincipalService principalService;

    private final CommentRepository commentRepository;

    @GetMapping("/commentTest")
    public Page<CommentDTO> commentPage(@RequestBody Map<String, Object> commentData, Criteria cri){
        long boardNo = Long.parseLong(commentData.get("boardNo").toString());
        int pageNum = Integer.parseInt(commentData.get("pageNum").toString());

        return commentRepository.getHierarchicalBoardCommentList(
                PageRequest.of(pageNum - 1
                , cri.getAmount()
                , Sort.by("commentGroupNo").descending()
                                .and(Sort.by("commentUpperNo").ascending()))
        , boardNo);

    }

    @GetMapping("/commentList")
    public ResponseEntity<CommentListDTO> commentList(@RequestBody Map<String, Object> boardNo, Criteria cri){
        boardNo.forEach((s, o) -> log.info(s + " : " + o));

        /**
         * commentList 요청시 받을 데이터
         * boardNo, imageNo, pageNum
         */

        return new ResponseEntity<>(commentService.commentList(boardNo, cri), HttpStatus.OK);
    }

    @PostMapping("/commentInsert")
    public int commentInsert(@RequestBody Map<String, Object> commentData
                                    , Comment comment
                                    , Principal principal){

        return commentService.commentInsert(commentData, comment, principal);
    }

    @PostMapping("/commentReply")
    public int commentReply(@RequestBody Map<String, Object> commentData
                                , Comment comment
                                , Principal principal){
        log.info("commentReply");


        return commentService.commentReplyInsert(commentData, comment, principal);
    }

    @DeleteMapping("/comentDelete")
    public int commentDelete(@RequestBody String commentNo) {
        log.info("commentDelete");


        return commentService.commentDelete(Long.parseLong(commentNo));
    }
}
