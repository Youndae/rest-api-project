package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardModifyDTO;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import com.example.boardrest.service.HierarchicalBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<HierarchicalBoardDTO>> hierarchicalBoardMain(@RequestParam(value = "pageNum") int pageNum
                                                                                , @RequestParam(value = "amount") int amount
                                                                                , @RequestParam(value = "keyword", required = false) String keyword
                                                                                , @RequestParam(value = "searchType", required = false) String searchType) {

        log.info("keyword : " + keyword);
        log.info("searchType : " + searchType);
        log.info("pageNum : " + pageNum);

        Criteria cri = Criteria.builder()
                                .pageNum(pageNum)
                                .boardAmount(amount)
                                .keyword(keyword)
                                .searchType(searchType)
                                .build();

        /*if(keyword != null){
            log.info("keyword is not null");
            cri = Criteria.builder()
                    .pageNum(pageNum)
                    .boardAmount(amount)
                    .keyword(keyword)
                    .searchType(searchType)
                    .build();
        }else if(keyword == null){
            log.info("keyword is null");
            cri = Criteria.builder()
                    .pageNum(pageNum)
                    .boardAmount(amount)
                    .keyword(keyword)
                    .searchType(searchType)
                    .build();
            System.out.println("controller cri : " + cri);
        }*/

        return new ResponseEntity<>(hierarchicalBoardService.getHierarchicalBoardList(cri), HttpStatus.OK);
    }


    @GetMapping("/board-detail/{boardNo}")
    public ResponseEntity<HierarchicalBoardDTO> hierarchicalBoardDetail(@PathVariable long boardNo){

//        HierarchicalBoardDTO dto = hierarchicalBoardRepository.findByBoardNo(boardNo);

        /*if(principal != null) {
            log.info("userId : " + principal.getName());
            dto = HierarchicalBoardDetailDTO.builder()
                    .detailData(hierarchicalBoardRepository.findByBoardNo(boardNo))
                    .uid(principal.getName())
                    .build();
        }else if(principal == null) {
            log.info("principal is nullable");
            dto = HierarchicalBoardDetailDTO.builder()
                    .detailData(hierarchicalBoardRepository.findByBoardNo(boardNo))
                    .uid(null)
                    .build();
        }*/

        return new ResponseEntity<>(hierarchicalBoardRepository.findByBoardNo(boardNo), HttpStatus.OK);
    }

    @GetMapping("/board-reply-info/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<HierarchicalBoardDTO> hierarchicalBoardReplyInfo(@PathVariable long boardNo){

        return new ResponseEntity<>(hierarchicalBoardRepository.findByBoardNo(boardNo), HttpStatus.OK);
    }

    @PostMapping("/board-insert")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardInsert(@RequestBody HierarchicalBoardDTO dto, Principal principal){
        log.info("boardInsert");

        if(principal == null)
            log.info("api server principal is null");
        else if(principal != null)
            log.info("api server principal is not null : " + principal.getName());

        return hierarchicalBoardService.insertBoard(dto, principal);
    }

    @GetMapping("/board-modify/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<HierarchicalBoardModifyDTO> hierarchicalBoardModify(@PathVariable long boardNo, Principal principal){

        log.info("modify");

        HierarchicalBoardModifyDTO dto = hierarchicalBoardService.getModifyData(boardNo, principal);

        log.info("modify dto : {}", dto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping("/board-modify")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardModify(@RequestBody HierarchicalBoardModifyDTO dto, Principal principal){
        log.info("Patch board");

        return hierarchicalBoardService.patchBoard(dto, principal);
    }

    @DeleteMapping("/board-delete/{boardNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardDelete(@PathVariable long boardNo, Principal principal){
        log.info("delete board");

        return hierarchicalBoardService.deleteBoard(boardNo, principal);
    }

    @PostMapping("/board-reply")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long hierarchicalBoardReply(@RequestBody HierarchicalBoardModifyDTO dto, Principal principal){
        log.info("reply board");

        long responseVal = hierarchicalBoardService.insertBoardReply(dto, principal);

        return responseVal;
    }

}
