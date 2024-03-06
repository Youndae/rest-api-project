package com.example.boardrest.service;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.repository.CommentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void commentListTest() {
        String boardNo = "100000";
        String imageNo = null;

        Criteria cri = new Criteria();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "coco";
            }
        };

        Page<BoardCommentDTO> dto = commentService.commentList(boardNo, imageNo, cri, principal);

        System.out.println(dto);
    }

    @Test
    void findById() {
        long commentNo = 863L;

        String writer = commentRepository
                            .findById(commentNo)
                            .orElseThrow(() -> new NullPointerException("NullPointerException"))
                            .getMember()
                            .getUserId();

        assertEquals(writer, "coco");
    }
}