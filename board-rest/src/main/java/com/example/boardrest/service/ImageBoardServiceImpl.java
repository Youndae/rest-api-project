package com.example.boardrest.service;

import com.example.boardrest.customException.CustomIOException;
import com.example.boardrest.customException.CustomNotFoundException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.common.business.PageCondition;
import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardDetail;
import com.example.boardrest.domain.dto.imageBoard.business.ImageBoardPatchDetail;
import com.example.boardrest.domain.dto.imageBoard.out.*;
import com.example.boardrest.domain.dto.imageBoard.in.ImageBoardRequest;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.domain.enumuration.ListAmount;
import com.example.boardrest.domain.enumuration.SaveImageKey;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    public PageResponse<ImageBoardListResponse> getImageBoardList(ListRequest request) {
        PageCondition condition = PageCondition.of(request, ListAmount.IMAGE_BOARD);
        Pageable pageable = PageRequest.of(condition.getPage() - 1,
                condition.getAmount(),
                Sort.by("imageNo").descending()
        );

        Page<ImageBoardListResponse> content = imageBoardRepository.findAllListByPageable(condition, pageable);

        return PageResponse.of(content);
    }

    @Override
    public ImageBoardDetailResponse getImageBoardDetail(long id) {
        ImageBoardDetail imageBoard = imageBoardRepository.findDetailById(id);

        if(imageBoard == null){
            log.warn("ImageBoardService.getImageBoardDetail :: imageBoard is null. id={}", id);
            throw new CustomNotFoundException(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage());
        }

        List<String> dataDTO = imageDataRepository.getImageDataNameList(id);

        return ImageBoardDetailResponse.of(imageBoard, dataDTO);
    }

    // 이미지 게시판 insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long imageBoardInsert(List<MultipartFile> images,
                                ImageBoardRequest dto,
                                String userId) {

        Member memberEntity = principalService.getMemberByUserId(userId);

        ImageBoard imageBoard = dto.toInsertEntity(memberEntity);
        List<ImageData> imageDataList = imageInsert(images,  1);

        imageBoardRepository.save(imageBoard);
        imageDataList.forEach(v -> v.setImageBoard(imageBoard));
        imageDataRepository.saveAll(imageDataList);

        return imageBoard.getId();
    }

    @Override
    public ImageBoardPatchDetailResponse getPatchData(long id, String userId) {
        ImageBoardPatchDetail imageBoard = imageBoardRepository.findPatchDetailById(id);
        principalService.validateUser(imageBoard.getWriter(), userId);
        List<ImageDataResponse> imageDataList = imageDataRepository.getImageDataList(id);

        return ImageBoardPatchDetailResponse.builder()
                .title(imageBoard.getTitle())
                .content(imageBoard.getContent())
                .imageList(imageDataList)
                .build();
    }

    // 이미지 게시판 patch
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long imageBoardPatch(List<MultipartFile> images,
                                List<String> deleteFiles,
                                long id,
                                ImageBoardRequest request,
                                String userId) {
        ImageBoard imageBoard = imageBoardRepository
                .findById(id)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "invalid patch imageNo : " + id));

        principalService.validateUser(imageBoard.getMember().getUserId(), userId);

        imageBoard.patchImageBoard(request);

        if(images != null){
            List<ImageData> saveImageData = imageInsert(images, imageDataRepository.countImageStep(id) + 1);
            saveImageData.forEach(v -> v.setImageBoard(imageBoard));
            imageDataRepository.saveAll(saveImageData);
        }

        if(deleteFiles != null) {
            imageFileService.deleteImageBoardFile(deleteFiles);
            imageDataRepository.deleteImageDataList(deleteFiles);
        }

        return id;
    }

    // 이미지 게시판 delete
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImageBoard(long id, String userId) {
        ImageBoard imageBoard = imageBoardRepository
                .findById(id)
                .orElseThrow(() -> new CustomNotFoundException(ErrorCode.BAD_REQUEST, "invalid deleteImageNo : " + id));

        principalService.validateUser(imageBoard.getMember().getUserId(), userId);

        List<String> deleteFiles = imageDataRepository.getImageDataNameList(id);
        imageFileService.deleteImageBoardFile(deleteFiles);
        imageDataRepository.deleteImageDataListByImageId(id);
        imageBoardRepository.deleteById(id);
    }

    // 이미지 파일 저장 및 imageData save 처리
    private List<ImageData> imageInsert(List<MultipartFile> images, int step) {
        List<ImageData> saveImageData = new ArrayList<>();

        for(MultipartFile image : images){
            try {
                Map<SaveImageKey, String> map = imageFileService.boardImageSave(image);
                saveImageData.add(ImageData.builder()
                        .imageName(map.get(SaveImageKey.SAVE_NAME))
                        .originName(map.get(SaveImageKey.ORIGIN_NAME))
                        .imageStep(step++)
                        .build()
                );
            } catch (Exception e) {
                log.error("File Transfer Error. Rollback save file");

                saveImageData.forEach(v ->
                                imageFileService.cleanupImageBoardFiles(v.getImageName())
                        );

                log.error("ImageBoardService.imageInsert :: Failed transfer file");

                throw new CustomIOException(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            }
        }

        return saveImageData;
    }
}
