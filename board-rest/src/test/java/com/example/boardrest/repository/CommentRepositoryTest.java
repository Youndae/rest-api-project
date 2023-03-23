package com.example.boardrest.repository;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.HierarchicalBoard;
import com.example.boardrest.domain.Member;
import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void listTest(){

        Page<BoardCommentDTO> dto = commentRepository.getHierarchicalBoardCommentList(
                PageRequest.of(1 - 1
                        , 20
                        , Sort.by("commentGroupNo").descending()
                                .and(Sort.by("commentUpperNo").ascending()))
                , 99988);

        System.out.println(dto);

//        System.out.println("total : " + total);
    }



}