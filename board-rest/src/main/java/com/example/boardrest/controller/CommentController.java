package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @GetMapping("/")
    public ResponseEntity<ResponsePageableListDTO<BoardCommentDTO>> getList(@RequestParam(value = "boardNo", required = false) String boardNo
                                                            , @RequestParam(value = "imageNo", required = false) String imageNo
                                                            , @RequestParam(value = "pageNum") int pageNum
                                                            , Principal principal){
        Criteria cri = Criteria.builder()
                .pageNum(pageNum)
                .build();

        return new ResponseEntity<>(commentService.commentList(boardNo, imageNo, cri, principal), HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long insertComment(@RequestBody CommentInsertDTO dto
                            , Principal principal) {

        return commentService.commentInsertProc(dto, principal);
    }

    @DeleteMapping("/{commentNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public int deleteComment(@PathVariable long commentNo, Principal principal) {

        return commentService.commentDelete(commentNo, principal);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long replyComment(@RequestBody CommentInsertDTO dto
                                , Principal principal){

        System.out.println("commentReply dto : " + dto);

        return commentService.commentInsertProc(dto, principal);
    }

}
