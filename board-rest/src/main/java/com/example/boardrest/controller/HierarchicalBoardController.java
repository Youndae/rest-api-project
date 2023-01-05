package com.example.boardrest.controller;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardListDTO;
import com.example.boardrest.domain.dto.PageDTO;
import com.example.boardrest.repository.CommentRepository;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import com.example.boardrest.service.HierarchicalBoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Slf4j
public class HierarchicalBoardController {

    private final HierarchicalBoardService hierarchicalBoardService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    @GetMapping("/boardList")
    public ResponseEntity<HierarchicalBoardListDTO> hierarchicalBoardMain(Criteria cri){


        return new ResponseEntity<>(hierarchicalBoardService.getHierarchicalBoardList(cri), HttpStatus.OK);

    }


    @GetMapping("/boardDetail/{boardNo}")
    public ResponseEntity<HierarchicalBoardDTO> hierarchicalBoardDetail(@PathVariable long boardNo){

        return new ResponseEntity<>(hierarchicalBoardRepository.findByBoardNo(boardNo), HttpStatus.OK);
    }

    @GetMapping("/boardReplyInfo/{boardNo}")
    public ResponseEntity<HierarchicalBoardDTO> hierarchicalBoardReplyInfo(@PathVariable long boardNo){

        return new ResponseEntity<>(hierarchicalBoardRepository.findByBoardNo(boardNo), HttpStatus.OK);
    }

    @PostMapping("/boardInsert")
    public long hierarchicalBoardInsert(HttpServletRequest request, Principal principal){
        log.info("boardInsert");

        return hierarchicalBoardService.insertBoard(request, principal);
    }

    @PatchMapping("/boardModify")
    public long hierarchicalBoardModify(HttpServletRequest request){
        log.info("Patch board");

        return hierarchicalBoardService.patchBoard(request);
    }

    @DeleteMapping("/boardDelete/{boardNo}")
    public long hierarchicalBoardDelete(@PathVariable long boardNo){
        log.info("delete board");

        return hierarchicalBoardService.deleteBoard(boardNo);
    }

    @PostMapping("/boardReply")
    public long hierarchicalBoardReply(HttpServletRequest request, Principal principal){
        log.info("reply board");

        return hierarchicalBoardService.insertBoardReply(request, principal);

    }

}
