package com.example.boardrest.repository;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.HierarchicalBoard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HierarchicalBoardRepositoryTest {

    @Autowired
    private HierarchicalBoardRepository hierarchicalBoardRepository;

    @Test
    void getListTest(){
        getListMethod();
    }

    List<HierarchicalBoard> getListMethod(){
        return hierarchicalBoardRepository.hierarchicalBoardList(
                PageRequest.of(1 - 1
                        , 20
                        , Sort.by("boardGroupNo").descending()
                                .and(Sort.by("boardUpperNo").ascending()))
        ).toList();
    }

}