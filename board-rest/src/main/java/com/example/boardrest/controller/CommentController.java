package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.comment.out.BoardCommentResponse;
import com.example.boardrest.domain.dto.comment.in.CommentRequest;
import com.example.boardrest.domain.dto.response.ApiResponse;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;

    @GetMapping("/board")
    public ResponseEntity<ApiResponse<PageResponse<BoardCommentResponse>>> getBoardCommentList(
                                                                @RequestParam(value = "id", required = false) @Min(value = 1) long id,
                                                                @RequestParam(value = "page", required = false) @Min(value = 1) int page
    ) {
        PageResponse<BoardCommentResponse> result = commentService.getBoardCommentList(id, page);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/image-board")
    public ResponseEntity<ApiResponse<PageResponse<BoardCommentResponse>>> getImageBoardCommentList(
                                                                @RequestParam(value = "id", required = false) @Min(value = 1) long id,
                                                                @RequestParam(value = "page", required = false) @Min(value = 1) int page
    ) {
        PageResponse<BoardCommentResponse> result = commentService.getImageBoardCommentList(id, page);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/board/{targetBoardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> insertBoardComment(@PathVariable(name = "targetBoardId") @Min(value = 1) long targetBoardId,
                                                   @RequestBody @Valid CommentRequest request,
                                                    Principal principal
    ) {

        commentService.insertBoardComment(targetBoardId, request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/image-board/{targetBoardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> insertImageBoardComment(@PathVariable(name = "targetBoardId") @Min(value = 1) long targetBoardId,
                                                        @RequestBody @Valid CommentRequest request,
                                                        Principal principal
    ) {

        commentService.insertImageBoardComment(targetBoardId, request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "id") @Min(value = 1) long id,
                                              Principal principal
    ) {

        commentService.deleteComment(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> replyComment(@PathVariable(name = "id") @Min(value = 1) long id,
                               @RequestBody @Valid CommentRequest dto,
                               Principal principal
    ){

        commentService.insertReplyComment(id, dto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
