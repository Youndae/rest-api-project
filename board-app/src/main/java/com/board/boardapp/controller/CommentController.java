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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
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

        log.info("cri.getPageNum() : {}, cri.getAmount() : {}", cri.getPageNum(), cri.getAmount());

        CommentListDTO dto = commentBoardWebClient.getBoardComment(boardNo, request, response, cri);

        log.info("controller dto : {}", dto);



        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/imageComment/{imageNo}/{pageNum}")
    public ResponseEntity<CommentListDTO> imageComment(@PathVariable("imageNo") long imageNo
            , @PathVariable("pageNum") int pageNum
            , HttpServletRequest request
            , HttpServletResponse response) throws JsonProcessingException {

        /*log.info("controller boardNo : {}, pageNum : {}", boardNo, pageNum);

        Criteria cri = new Criteria();
        cri.setPageNum(pageNum);

        log.info("cri.getPageNum() : {}, cri.getAmount() : {}", cri.getPageNum(), cri.getAmount());

        CommentListDTO dto = commentBoardWebClient.getBoardComment(boardNo, request, response, cri);

        log.info("controller dto : {}", dto);*/



//        return new ResponseEntity<>(dto, HttpStatus.OK);

        return null;
    }
}
