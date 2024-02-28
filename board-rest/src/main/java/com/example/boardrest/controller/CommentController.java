package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.BoardCommentListDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @GetMapping("/comment-list")
    public ResponseEntity<BoardCommentListDTO> commentList(@RequestParam(value = "boardNo", required = false) String boardNo
                                                        , @RequestParam(value = "imageNo", required = false) String imageNo
                                                        , @RequestParam(value = "pageNum") int pageNum
                                                        , @RequestParam(value = "amount") int amount
                                                        , Principal principal){

        Criteria cri = Criteria.builder()
                .pageNum(pageNum)
                .boardAmount(amount)
                .build();

        return new ResponseEntity<>(commentService.commentList(boardNo, imageNo, cri, principal), HttpStatus.OK);
    }

    @PostMapping("/comment-insert")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long commentInsert(@RequestBody CommentInsertDTO dto
                            , Principal principal) {

        return commentService.commentInsertProc(dto, principal);
    }

    @PostMapping("/comment-reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long commentReply(@RequestBody CommentInsertDTO dto
                                , Principal principal){

        return commentService.commentInsertProc(dto, principal);
    }

    @DeleteMapping("/comment-delete/{commentNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public int commentDelete(@PathVariable long commentNo, Principal principal) {

        return commentService.commentDelete(commentNo, principal);
    }

}
