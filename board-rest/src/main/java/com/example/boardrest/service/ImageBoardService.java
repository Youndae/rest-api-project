package com.example.boardrest.service;

import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDetailDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageDataDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageModifyInfoDTO;
import com.example.boardrest.domain.dto.iBoard.in.ImageBoardRequestDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    Page<ImageBoardDTO> getImageBoardList(Criteria cri);

    ImageBoardDetailDTO getImageBoardDetail(long imageNo);

    long imageInsertCheck(List<MultipartFile> images
                            , ImageBoardRequestDTO dto
                            , Principal principal);

    long imagePatchCheck(List<MultipartFile> images
                        , List<String> deleteFiles
                        , long imageNo
                        , ImageBoardRequestDTO dto
                        , Principal principal);

    String deleteImageBoard(long imageNo, Principal principal);

    ImageModifyInfoDTO getModifyData(long imageNo, Principal principal);

    List<ImageDataDTO> getModifyImageAttach(long imageNo);

    ResponseEntity<byte[]> getFile(String imageName);
}
