package com.example.boardrest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HierarchicalBoardServiceImplTest {

    @Autowired
    private HierarchicalBoardService service;

    @Test
    void hTest() {
        long boardNo = 100018L;

//        service.deleteBoard(boardNo);
    }

}