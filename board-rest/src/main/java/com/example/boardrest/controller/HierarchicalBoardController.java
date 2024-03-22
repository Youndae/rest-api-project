package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import com.example.boardrest.service.HierarchicalBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Slf4j
public class HierarchicalBoardController {

    private final HierarchicalBoardService hierarchicalBoardService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    @GetMapping("/board-list")
    public ResponseEntity<ResponsePageableListDTO> hierarchicalBoardMain(@RequestParam(value = "pageNum") int pageNum
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


    @GetMapping("/board-detail/{boardNo}")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO>> hierarchicalBoardDetail(@PathVariable long boardNo, Principal principal){

        return new ResponseEntity<>(hierarchicalBoardService.getBoardDetail(boardNo, principal), HttpStatus.OK);
    }

    @GetMapping("/board-reply-info/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO>> hierarchicalBoardReplyInfo(@PathVariable long boardNo, Principal principal){

        return new ResponseEntity<>(hierarchicalBoardService.getReplyInfo(boardNo, principal), HttpStatus.OK);
    }

    @PostMapping("/board-insert")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardInsert(@RequestBody HierarchicalBoardDTO dto, Principal principal){

        return hierarchicalBoardService.insertBoard(dto, principal);
    }

    @GetMapping("/board-modify/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO>> hierarchicalBoardModify(@PathVariable long boardNo, Principal principal){


        return new ResponseEntity<>(hierarchicalBoardService.getModifyData(boardNo, principal), HttpStatus.OK);
    }

    @PatchMapping("/board-modify")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardModify(@RequestBody HierarchicalBoardModifyDTO dto, Principal principal){

        return hierarchicalBoardService.patchBoard(dto, principal);
    }

    @DeleteMapping("/board-delete/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardDelete(@PathVariable long boardNo, Principal principal){

        return hierarchicalBoardService.deleteBoard(boardNo, principal);
    }

    @PostMapping("/board-reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardReply(@RequestBody HierarchicalBoardReplyDTO dto, Principal principal){

        long responseValue = hierarchicalBoardService.insertBoardReply(dto, principal);

        return responseValue;
    }

}
