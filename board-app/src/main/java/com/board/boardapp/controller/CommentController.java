package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.CommentBoardWebClient;
import com.board.boardapp.dto.CommentDTO;
import com.board.boardapp.dto.CommentListDTO;
import com.board.boardapp.dto.Criteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentBoardWebClient commentBoardWebClient;

    @GetMapping("/boardComment/{boardNo}/{pageNum}")
    public ResponseEntity<CommentListDTO> boardComment(@PathVariable("boardNo") long boardNo
                                    , @PathVariable("pageNum") int pageNum
                                    , HttpServletRequest request
                                    , HttpServletResponse response) throws JsonProcessingException {

        log.info("controller boardNo : {}, pageNum : {}", boardNo, pageNum);

        Criteria cri = new Criteria();
        cri.setPageNum(pageNum);

        CommentListDTO dto = commentBoardWebClient.getBoardComment(boardNo, request, response, cri);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/imageComment/{imageNo}/{pageNum}")
    public ResponseEntity<CommentListDTO> imageComment(@PathVariable("imageNo") long imageNo
            , @PathVariable("pageNum") int pageNum
            , HttpServletRequest request
            , HttpServletResponse response) throws JsonProcessingException {


        Criteria cri = new Criteria();
        cri.setPageNum(pageNum);

        CommentListDTO dto = commentBoardWebClient.getImageComment(imageNo, request, response, cri);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/commentInsert")
    public long commentInsert(@RequestBody Map<String, Object> commentData
                                , HttpServletRequest request
                                , HttpServletResponse response){

        return commentBoardWebClient.commentInsert(commentData, request, response);
    }

    @PostMapping("/commentReplyInsert")
    public long commentReplyInsert(@RequestBody Map<String, Object> commentData
                                        , HttpServletRequest request
                                        , HttpServletResponse response){


        return commentBoardWebClient.commentReplyInsert(commentData, request, response);
    }

    @DeleteMapping("/commentDelete/{commentNo}")
    public int commentDelete(@PathVariable long commentNo
                                , HttpServletRequest request
                                , HttpServletResponse response){


        return commentBoardWebClient.commentDelete(commentNo, request, response);
    }
}
