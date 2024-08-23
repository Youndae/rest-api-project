package com.board.boardapp.controller;

import com.board.boardapp.connection.webClient.CommentBoardWebClient;
import com.board.boardapp.domain.dto.CommentDTO;
import com.board.boardapp.domain.dto.Criteria;
import com.board.boardapp.domain.dto.PaginationListDTO;
import com.board.boardapp.domain.dto.comment.in.CommentInsertDTO;
import com.board.boardapp.domain.dto.comment.in.CommentReplyInsertDTO;
import com.board.boardapp.service.CookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentBoardWebClient commentBoardWebClient;

    private final CookieService cookieService;

    @GetMapping("/{board}/{boardNo}/{pageNum}")
    public ResponseEntity<PaginationListDTO<CommentDTO>> getList(@PathVariable("board") String board
                                                , @PathVariable("boardNo") long boardNo
                                                , @PathVariable("pageNum") int pageNum
                                                , HttpServletRequest request
                                                , HttpServletResponse response) {
        Criteria cri = new Criteria(pageNum);

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);
        PaginationListDTO<CommentDTO> dto = commentBoardWebClient.getList(board, boardNo, cri, cookieMap, response);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/")
    public String commentInsert(@RequestBody CommentInsertDTO dto
            , HttpServletRequest request
            , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return commentBoardWebClient.commentInsert(dto, cookieMap, response);
    }

    @PostMapping("/reply")
    public String commentReplyInsert(@RequestBody CommentReplyInsertDTO dto
            , HttpServletRequest request
            , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return commentBoardWebClient.commentReplyInsert(dto, cookieMap, response);
    }

    @DeleteMapping("/{commentNo}")
    public String commentDelete(@PathVariable long commentNo
                                , HttpServletRequest request
                                , HttpServletResponse response){

        MultiValueMap<String, String> cookieMap = cookieService.setCookieToMultiValueMap(request);

        return commentBoardWebClient.commentDelete(commentNo, cookieMap, response);
    }
}
