package com.example.boardrest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
class HierarchicalBoardRepositoryTest {

    @Autowired
    private HierarchicalBoardRepository hierarchicalBoardRepository;

    @Test
    void getListTest(){
        hierarchicalBoardRepository.hierarchicalBoardListSearchContent(
                "%게시글%"
                , PageRequest.of(0
                , 20
                , Sort.by("boardGroupNo").descending()
                                .and(Sort.by("boardUpperNo").ascending()))
        );
    }


}