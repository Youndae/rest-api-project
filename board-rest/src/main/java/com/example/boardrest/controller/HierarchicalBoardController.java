package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardDetailDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardReplyInfoDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.factory.ResponseFactory;
import com.example.boardrest.domain.mapper.CriteriaRequestMapper;
import com.example.boardrest.service.HierarchicalBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final ResponseFactory responseFactory;

    @GetMapping("/")
    public ResponseEntity<ResponsePageableListDTO<HierarchicalBoardListDTO>> getBoardList(@RequestParam(value = "pageNum") int pageNum
                                                                        , @RequestParam(value = "keyword", required = false) String keyword
                                                                        , @RequestParam(value = "searchType", required = false) String searchType
                                                                        , Principal principal) {

        Criteria cri = CriteriaRequestMapper.fromBoardRequest(pageNum, keyword, searchType);

        Page<HierarchicalBoardListDTO> dto = hierarchicalBoardService.getHierarchicalBoardList(cri);

        return responseFactory.createListResponse(dto, principal);
    }

    @GetMapping("/{boardNo}")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO>> getDetail(@PathVariable long boardNo, Principal principal){

        HierarchicalBoardDetailDTO dto = hierarchicalBoardService.getBoardDetail(boardNo);

        return responseFactory.createDetailResponse(dto, principal);
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long insertBoard(@RequestBody HierarchicalBoardDTO dto, Principal principal){

        return hierarchicalBoardService.insertBoard(dto, principal);
    }

    @GetMapping("/patch-detail/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO>> getPatchDetail(@PathVariable long boardNo, Principal principal){

        HierarchicalBoardModifyDTO dto = hierarchicalBoardService.getModifyData(boardNo, principal);

        return responseFactory.createDetailResponse(dto, principal);
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
    public String deleteBoard(@PathVariable long boardNo, Principal principal){

        return hierarchicalBoardService.deleteBoard(boardNo, principal);
    }

    @GetMapping("/reply/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO>> getReplyDetail(@PathVariable long boardNo, Principal principal){

        HierarchicalBoardReplyInfoDTO dto = hierarchicalBoardService.getReplyInfo(boardNo);

        return responseFactory.createDetailResponse(dto, principal);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long insertReply(@RequestBody HierarchicalBoardReplyDTO dto, Principal principal){

        return hierarchicalBoardService.insertBoardReply(dto, principal);
    }

}
