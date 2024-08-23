package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Test
    @DisplayName("list 조회 테스트")
    void boardListTest() {
        Criteria cri = new Criteria();

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getBoardAmount()
                , Sort.by("boardGroupNo").descending()
                        .and(Sort.by("boardUpperNo").ascending()));

        Page<HierarchicalBoardListDTO> dto = hierarchicalBoardRepository.findAll(cri, pageable);


    }
}