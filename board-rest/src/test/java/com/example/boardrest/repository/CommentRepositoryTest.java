package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.service.CountTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.sql.Date;
import java.time.LocalDate;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CountTableService countTableService;

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

    @Test
    void saveImageComment(){
        long imageNo = 273;

        for(int i = 518; i < 761; i++){
            commentRepository.save(Comment.builder()
                    .member(Member.builder().userId("mozzi").build())
                    .commentDate(Date.valueOf(LocalDate.now()))
                    .commentContent("imageCommentContent" + i)
                    .commentGroupNo(i)
                    .commentIndent(1)
                    .commentUpperNo(String.valueOf(i))
                    .imageBoard(ImageBoard.builder().imageNo(imageNo).imageTitle("testInsert273").build())
                    .build()
            );
        }

    }

    @Test
    void countTableTest(){
        countTableService.boardCountMinus("imageboard");
    }



}