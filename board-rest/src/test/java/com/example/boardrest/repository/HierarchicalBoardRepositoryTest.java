package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.HierarchicalBoard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@SpringBootTest
class HierarchicalBoardRepositoryTest {

    @Autowired
    private HierarchicalBoardRepository hierarchicalBoardRepository;


    @Test
    void writerTest(){
        String uid = "coco";
        long boardNo = 100000L;

        Optional<HierarchicalBoard> board = hierarchicalBoardRepository.findById(boardNo);

        if(!board.get().getMember().getUserId().equals(uid))
            System.out.println("false");
        else
            System.out.println("true");
    }


}