package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.comment.out.BoardCommentDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.comment.in.CommentInsertDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.factory.ResponseFactory;
import com.example.boardrest.domain.mapper.CriteriaRequestMapper;
import com.example.boardrest.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private ResponseFactory responseFactory;

    @GetMapping("/")
    public ResponseEntity<ResponsePageableListDTO<BoardCommentDTO>> getList(@RequestParam(value = "boardNo", required = false) String boardNo
                                                            , @RequestParam(value = "imageNo", required = false) String imageNo
                                                            , @RequestParam(value = "pageNum") int pageNum
                                                            , Principal principal){
        Criteria cri = CriteriaRequestMapper.fromCommentRequest(pageNum);
        Page<BoardCommentDTO> dto = commentService.commentList(boardNo, imageNo, cri);

        return responseFactory.createListResponse(dto, principal);
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public String insertComment(@RequestBody CommentInsertDTO dto
                            , Principal principal) {

        return commentService.commentInsertProc(dto, principal);
    }

    @DeleteMapping("/{commentNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public String deleteComment(@PathVariable long commentNo, Principal principal) {

        return commentService.commentDelete(commentNo, principal);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public String replyComment(@RequestBody CommentInsertDTO dto
                                , Principal principal){

        return commentService.commentInsertProc(dto, principal);
    }

}
