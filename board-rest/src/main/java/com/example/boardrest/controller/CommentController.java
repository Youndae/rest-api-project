package com.example.boardrest.controller;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.Member;
import com.example.boardrest.service.CommentService;
import com.example.boardrest.service.PrincipalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/commentList")
    public ResponseEntity<List<Comment>> commentList(Comment comment){
        log.info("imageNo : " + comment.getImageBoard().getImageNo());
        log.info("boardNo : " + comment.getHierarchicalBoard().getBoardNo());

        return new ResponseEntity<>(commentService.commentList(comment), HttpStatus.OK);
    }

    @PostMapping("/commentInsert")
    public int commentInsert(@RequestBody Map<String, Object> commentData
                                    , Comment comment
                                    , Principal principal){

        return commentService.commentInsert(commentData, comment);
    }
}
