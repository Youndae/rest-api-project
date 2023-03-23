package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.ImageBoardDetailDTO;
import com.example.boardrest.domain.dto.ImageDataDTO;
import com.example.boardrest.domain.dto.ImageDetailDTO;
import com.example.boardrest.domain.dto.ImageDetailDataDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageBoardRepositoryTest {

    @Autowired
    private ImageBoardRepository repository;

    @Autowired
    private ImageDataRepository imageDataRepository;

    @Test
    public void imageDataTest(){
        long imageNo = 10;

        ImageDetailDTO imageDTO = repository.imageDetailDTO(imageNo);

        List<ImageDetailDataDTO> imageDataList = imageDataRepository.getImageData(imageNo);

        ImageBoardDetailDTO dto = ImageBoardDetailDTO.builder()
                .imageNo(imageDTO.getImageNo())
                .userId(imageDTO.getUserId())
                .imageContent(imageDTO.getImageContent())
                .imageDate(imageDTO.getImageDate())
                .imageTitle(imageDTO.getImageTitle())
                .imageData(imageDataList)
                .build();

        System.out.println(dto);
    }



}