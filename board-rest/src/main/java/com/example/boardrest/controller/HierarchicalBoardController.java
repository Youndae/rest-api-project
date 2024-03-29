package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.service.HierarchicalBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Slf4j
public class HierarchicalBoardController {

    private final HierarchicalBoardService hierarchicalBoardService;

    @GetMapping("/")
    public ResponseEntity<ResponsePageableListDTO> getBoardList(@RequestParam(value = "pageNum") int pageNum
                                                                        , @RequestParam(value = "keyword", required = false) String keyword
                                                                        , @RequestParam(value = "searchType", required = false) String searchType
                                                                        , Principal principal) {

        Criteria cri = Criteria.builder()
                                .pageNum(pageNum)
                                .keyword(keyword)
                                .searchType(searchType)
                                .build();

        return new ResponseEntity<>(hierarchicalBoardService.getHierarchicalBoardList(cri, principal), HttpStatus.OK);
    }

    @GetMapping("/{boardNo}")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO>> getDetail(@PathVariable long boardNo, Principal principal){

        return new ResponseEntity<>(hierarchicalBoardService.getBoardDetail(boardNo, principal), HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long insertBoard(@RequestBody HierarchicalBoardDTO dto, Principal principal){

        return hierarchicalBoardService.insertBoard(dto, principal);
    }

    @GetMapping("/patch-detail/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO>> getPatchDetail(@PathVariable long boardNo, Principal principal){

        return new ResponseEntity<>(hierarchicalBoardService.getModifyData(boardNo, principal), HttpStatus.OK);
    }

    @PatchMapping("/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long patchBoard(@RequestBody HierarchicalBoardModifyDTO dto
                            , @PathVariable long boardNo
                            , Principal principal){

        return hierarchicalBoardService.patchBoard(dto, boardNo, principal);
    }

    @DeleteMapping("/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long deleteBoard(@PathVariable long boardNo, Principal principal){

        return hierarchicalBoardService.deleteBoard(boardNo, principal);
    }

    @GetMapping("/reply/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO>> getReplyDetail(@PathVariable long boardNo, Principal principal){

        return new ResponseEntity<>(hierarchicalBoardService.getReplyInfo(boardNo, principal), HttpStatus.OK);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long insertReply(@RequestBody HierarchicalBoardReplyDTO dto, Principal principal){

        long responseValue = hierarchicalBoardService.insertBoardReply(dto, principal);

        return responseValue;
    }

}
