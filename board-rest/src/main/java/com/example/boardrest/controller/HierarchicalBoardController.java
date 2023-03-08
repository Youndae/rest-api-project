package com.example.boardrest.controller;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardDetailDTO;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import com.example.boardrest.service.HierarchicalBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    @GetMapping("/board-list")
    public ResponseEntity<Page<HierarchicalBoardDTO>> hierarchicalBoardMain(@RequestParam(value = "pageNum") int pageNum
                                                                                , @RequestParam(value = "amount") int amount
                                                                                , @RequestParam(value = "keyword", required = false) String keyword
                                                                                , @RequestParam(value = "searchType", required = false) String searchType){

        log.info("keyword : " + keyword);
        log.info("pageNum : " + pageNum);

        Criteria cri = new Criteria();

        if(keyword != null){
            log.info("keyword is not null");
            cri = Criteria.builder()
                    .pageNum(pageNum)
                    .amount(amount)
                    .keyword(keyword)
                    .searchType(searchType)
                    .build();
        }else if(keyword == null){
            log.info("keyword is null");
            cri = Criteria.builder()
                    .pageNum(pageNum)
                    .amount(amount)
                    .build();
        }

        return new ResponseEntity<>(hierarchicalBoardService.getHierarchicalBoardList(cri), HttpStatus.OK);
    }


    @GetMapping("/board-detail/{boardNo}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<HierarchicalBoardDetailDTO> hierarchicalBoardDetail(@PathVariable long boardNo, Principal principal){

        log.info("detail");

        if(principal != null)
            log.info("userId : " + principal.getName());
        else if(principal == null)
            log.info("principal is nullable");

        HierarchicalBoardDetailDTO dto = HierarchicalBoardDetailDTO.builder()
                .detailData(hierarchicalBoardRepository.findByBoardNo(boardNo))
                .uid("coco")
                .build();

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/board-reply-info/{boardNo}")
    public ResponseEntity<HierarchicalBoardDTO> hierarchicalBoardReplyInfo(@PathVariable long boardNo){

        return new ResponseEntity<>(hierarchicalBoardRepository.findByBoardNo(boardNo), HttpStatus.OK);
    }

    @PostMapping("/board-insert")
    public long hierarchicalBoardInsert(@RequestBody HierarchicalBoardDTO dto, Principal principal){
        log.info("boardInsert");

        if(principal == null)
            log.info("api server principal is null");
        else if(principal != null)
            log.info("api server principal is not null : " + principal.getName());

        log.info("title : {}, content : {}", dto.getBoardTitle(), dto.getBoardContent());

//        log.info("title : {}", request.getParameter("boardTitle"));
//        log.info("content : {}", request.getParameter("boardContent"));

        return 1L;

//        return hierarchicalBoardService.insertBoard(request, principal);
    }

    @PatchMapping("/board-modify")
    public long hierarchicalBoardModify(HttpServletRequest request){
        log.info("Patch board");

        return hierarchicalBoardService.patchBoard(request);
    }

    @DeleteMapping("/board-delete/{boardNo}")
    public long hierarchicalBoardDelete(@PathVariable long boardNo){
        log.info("delete board");

        return hierarchicalBoardService.deleteBoard(boardNo);
    }

    @PostMapping("/board-reply")
    public long hierarchicalBoardReply(HttpServletRequest request, Principal principal){
        log.info("reply board");

        return hierarchicalBoardService.insertBoardReply(request, principal);

    }

}
