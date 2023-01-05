package com.example.boardrest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void listTest(){
        commentRepository.getHierarchicalBoardCommentList(
                PageRequest.of(0
                        , 20
                        , Sort.by("commentGroupNo").descending()
                                .and(Sort.by("commentUpperNo").ascending()))
                , 99988L
        );

//        System.out.println("total : " + total);
    }

}