package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.board.in.BoardReplyRequest;
import com.example.boardrest.domain.dto.board.out.BoardDetailResponse;
import com.example.boardrest.domain.dto.board.out.BoardListResponse;
import com.example.boardrest.domain.dto.board.out.BoardPatchDetailResponse;
import com.example.boardrest.domain.dto.board.in.BoardRequest;
import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.response.ApiResponse;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<BoardListResponse>>> getBoardList(ListRequest listRequest) {
        listRequest.validate();
        log.info("boardList request : {}", listRequest);
        PageResponse<BoardListResponse> dto = boardService.getBoardList(listRequest);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getDetail(@PathVariable(name = "id") @Min(value = 1) long id){

        BoardDetailResponse dto = boardService.getBoardDetail(id);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> insertBoard(@RequestBody @Valid BoardRequest dto, Principal principal){

        Long result = boardService.insertBoard(dto, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result));
    }

    @GetMapping("/patch-detail/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BoardPatchDetailResponse>> getPatchDetail(@PathVariable(name = "id") @Min(value = 1) long id, Principal principal){

        BoardPatchDetailResponse result = boardService.getPatchData(id, principal.getName());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> patchBoard(@RequestBody @Valid BoardRequest request,
                            @PathVariable(name = "id") @Min(value = 1) long id,
                            Principal principal){
        Long result = boardService.patchBoard(request, id, principal.getName());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBoard(@PathVariable(name = "id") @Min(value = 1) long id, Principal principal){

        boardService.deleteBoard(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reply/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> getReplyDetail(@PathVariable(name = "id") @Min(value = 1) long id){

        boardService.getReplyInfo(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reply/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> insertReply(
            @PathVariable(name = "id") @Min(value = 1) long id,
            @RequestBody @Valid BoardReplyRequest request,
            Principal principal
    ){

        Long result = boardService.insertBoardReply(id, request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result));
    }

}
