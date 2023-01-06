package com.example.boardrest.controller;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardCommentDTO;
import com.example.boardrest.domain.dto.CommentListDTO;
import com.example.boardrest.repository.CommentRepository;
import com.example.boardrest.service.CommentService;
import com.example.boardrest.service.PrincipalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    private final CommentRepository commentRepository;

    @GetMapping("/commentList")
    public ResponseEntity<CommentListDTO> commentList(@RequestBody Map<String, Object> commentData, Criteria cri){
        commentData.forEach((s, o) -> log.info(s + " : " + o));

        /**
         * commentList 요청시 받을 데이터
         * boardNo, imageNo, pageNum
         */

        return new ResponseEntity<>(commentService.commentList(commentData, cri), HttpStatus.OK);
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
