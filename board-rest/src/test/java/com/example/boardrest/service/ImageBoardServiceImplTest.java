package com.example.boardrest.service;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.ImageBoardDTO;
import com.example.boardrest.repository.ImageBoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageBoardServiceImplTest {

    @Autowired
    private ImageBoardRepository repository;

    @DisplayName("이미지 게시판 리스트 조회")
    @Test
    void imageList() {
        Criteria cri = new Criteria();

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getImageAmount()
                , Sort.by("imageNo").descending()
        );

        Page<ImageBoardDTO> dto = repository.findAll(cri, pageable);

        dto.stream().forEach(System.out::println);
    }


}