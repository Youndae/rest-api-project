package com.example.boardrest.service;

import com.example.boardrest.customException.CustomIOException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDetailDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageDataDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageModifyInfoDTO;
import com.example.boardrest.domain.dto.iBoard.in.ImageBoardRequestDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.Result;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardServiceImpl implements ImageBoardService{

    private final PrincipalService principalService;

    private final ImageBoardRepository imageBoardRepository;

    private final ImageDataRepository imageDataRepository;

    private final ImageFileService imageFileService;


    @Override
    public Page<ImageBoardDTO> getImageBoardList(Criteria cri) {
        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getImageAmount()
                , Sort.by("imageNo").descending()
        );

        return imageBoardRepository.findAll(cri, pageable);
    }

    /**
     *
     * ImageBoardDetailDTO 의 객체 생성을 메소드에서 담당하면 SRP 위배다.
     *
     */
    @Override
    public ImageBoardDetailDTO getImageBoardDetail(long imageNo) {
        ImageBoard imageBoard = imageBoardRepository
                                    .findById(imageNo)
                                    .orElseThrow(() -> new IllegalArgumentException("invalid detail imageNo : " + imageNo));

        List<ImageDataDTO> dataDTO = imageDataRepository.getImageData(imageNo);

        return ImageBoardDetailDTO.fromEntity(imageBoard, dataDTO);
    }

    // 이미지 게시판 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long imageInsertCheck(List<MultipartFile> images
                                , ImageBoardRequestDTO dto
                                , Principal principal) {

        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        ImageBoard imageBoard = dto.toInsertEntity(memberEntity);

        imageInsert(images,  1, imageBoard);

        return imageBoardRepository.save(imageBoard).getImageNo();
    }

    // 이미지 게시판 patch
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long imagePatchCheck(List<MultipartFile> images
                                , List<String> deleteFiles
                                , long imageNo
                                , ImageBoardRequestDTO dto
                                , Principal principal) {

        ImageBoard imageBoard = imageBoardRepository
                .findById(imageNo)
                .orElseThrow(() -> new IllegalArgumentException("invalid patch imageNo : " + imageNo));

        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        principalService.validateUser(imageBoard, principal);

        imageBoard = dto.toPatchEntity(memberEntity, imageBoard);

        if(images != null)
            imageInsert(images, imageDataRepository.countImageStep(imageNo) + 1, imageBoard);

        if(deleteFiles != null) {
            imageFileService.deleteImageBoardFile(deleteFiles);
            imageDataRepository.deleteImageDataList(deleteFiles);
        }

        return imageBoardRepository.save(imageBoard).getImageNo();
    }

    // 이미지 게시판 delete
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteImageBoard(long imageNo, Principal principal) {
        ImageBoard imageBoard = imageBoardRepository
                .findById(imageNo)
                .orElseThrow(() -> new IllegalArgumentException("invalid deleteImageNo : " + imageNo));

        principalService.validateUser(imageBoard, principal);

        List<String> deleteFiles = imageDataRepository.getDeleteImageDataList(imageNo);
        imageFileService.deleteImageBoardFile(deleteFiles);
        imageDataRepository.deleteImageDataList(deleteFiles);
        imageBoardRepository.deleteById(imageNo);

        return Result.SUCCESS.getResultMessage();
    }

    // 이미지 파일 저장 및 imageData save 처리
    private void imageInsert(List<MultipartFile> images
                            , int step
                            , ImageBoard imageBoard) {
        String filePath = FilePathProperties.BOARD_FILE_PATH;
        Map<String, String> map;

        for(MultipartFile image : images){
            try {
                map = imageFileService.saveFile(filePath, image);
                imageBoard.addImageData(ImageData.builder()
                        .imageName(map.get("imageName"))
                        .oldName(map.get("oldName"))
                        .imageStep(step++)
                        .build()
                );
            } catch (Exception e) {
                log.error("File Transfer Error. Rollback save file");

                imageBoard.getImageDataSet()
                        .forEach(v ->
                                imageFileService.deleteFile(filePath, v.getImageName())
                        );

                throw new CustomIOException(ErrorCode.IO_EXCEPTION, "File Transfer IOException");
            }
        }

    }

    @Override
    public ImageModifyInfoDTO getModifyData(long imageNo, Principal principal) {
        ImageBoard imageBoard = imageBoardRepository.findById(imageNo).orElseThrow(() -> new IllegalArgumentException("invalid modifyData imageNo : " + imageNo));
        principalService.validateUser(imageBoard, principal);

        return imageBoardRepository.findPatchDetail(imageNo);
    }

    @Override
    public List<ImageDataDTO> getModifyImageAttach(long imageNo) {

        return imageDataRepository.getImageData(imageNo);
    }

    @Override
    public ResponseEntity<byte[]> getFile(String imageName) {
        File file = new File(FilePathProperties.BOARD_FILE_PATH + imageName);
        ResponseEntity<byte[]> result = null;

        try{
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), HttpStatus.OK);
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
