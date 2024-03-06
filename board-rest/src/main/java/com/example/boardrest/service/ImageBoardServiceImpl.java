package com.example.boardrest.service;

import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardServiceImpl implements ImageBoardService{

    private final PrincipalService principalService;

    private final ImageBoardRepository imageBoardRepository;

    private final ImageDataRepository imageDataRepository;

    @Override
    public ImageBoardDetailDTO getImageBoardDetail(long imageNo) {
        ImageBoard imageBoard = imageBoardRepository
                                    .findById(imageNo)
                                    .orElseThrow(() -> new NullPointerException("nullPointerException"));

        List<ImageDataDTO> dataDTO = imageDataRepository.getImageData(imageNo);

        ImageBoardDetailDTO dto = ImageBoardDetailDTO.builder()
                                                .imageNo(imageBoard.getImageNo())
                                                .imageTitle(imageBoard.getImageTitle())
                                                .userId(imageBoard.getMember().getUserId())
                                                .imageContent(imageBoard.getImageContent())
                                                .imageDate(imageBoard.getImageDate())
                                                .imageData(dataDTO)
                                                .build();

        return dto;
    }

    @Override
    public Page<ImageBoardDTO> getImageBoardList(Criteria cri) {
        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
            , cri.getImageAmount()
                , Sort.by("imageNo").descending()
        );

        return imageBoardRepository.findAll(cri, pageable);
    }

    // 이미지 게시판 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long imageInsertCheck(List<MultipartFile> images
                                , String imageTitle
                                , String imageContent
                                , HttpServletRequest request
                                , Principal principal) {

        ImageBoard imageBoard = ImageBoard.builder()
                                        .member(principalService.checkPrincipal(principal))
                                        .imageTitle(request.getParameter("imageTitle"))
                                        .imageContent(request.getParameter("imageContent"))
                                        .imageDate(Date.valueOf(LocalDate.now()))
                                        .build();



        imageInsert(images,  1, imageBoard);

        return imageBoardRepository.save(imageBoard).getImageNo();
    }

    // 이미지 게시판 patch
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long imagePatchCheck(List<MultipartFile> images
                                , List<String> deleteFiles
                                , HttpServletRequest request
                                , Principal principal) {

        long imageNo = Long.parseLong(request.getParameter("imageNo"));

        ImageBoard imageBoard = imageBoardRepository
                .findById(imageNo)
                .orElseThrow(() -> new NullPointerException("NullPointerException"));

        String writer = imageBoard.getMember().getUserId();


        if(!writer.equals(principal.getName()))
            new AccessDeniedException("AccessDenied");

        imageBoard = ImageBoard.builder()
                                .member(principalService.checkPrincipal(principal))
                                .imageNo(imageNo)
                                .imageTitle(request.getParameter("imageTitle"))
                                .imageContent(request.getParameter("imageContent"))
                                .imageDate(imageBoard.getImageDate())
                                .build();

        if(deleteFiles != null) {
            deleteFilesProc(deleteFiles);
            imageDataRepository.deleteImageDataList(deleteFiles);
        }

        if(images != null)
            imageInsert(images, imageDataRepository.countImageStep(imageNo) + 1, imageBoard);

        return imageBoardRepository.save(imageBoard).getImageNo();
    }

    // 이미지 게시판 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long deleteImageBoard(long imageNo, Principal principal) {
        String writer = imageBoardRepository
                                .findById(imageNo)
                                .orElseThrow(() -> new NullPointerException("NullPointerException"))
                                .getMember()
                                .getUserId();

        if(!writer.equals(principal.getName()))
            new AccessDeniedException("AccessDenied");

        List<String> deleteFiles = imageDataRepository.getDeleteImageDataList(imageNo);
        deleteFilesProc(deleteFiles);
        imageDataRepository.deleteImageDataList(deleteFiles);
        imageBoardRepository.deleteById(imageNo);

        return 1;
    }

    // 이미지 파일 저장 및 imageData save 처리
    void imageInsert(List<MultipartFile> images
                            , int step
                            , ImageBoard imageBoard) {
        String filePath = FilePathProperties.FILE_PATH;

        for(MultipartFile image : images){
            String originalName = image.getOriginalFilename();
            StringBuffer sb = new StringBuffer();
            String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                                        .format(System.currentTimeMillis()))
                                .append(UUID.randomUUID().toString())
                                .append(originalName.substring(originalName.lastIndexOf(".")))
                                .toString();
            String saveFile = filePath + saveName;

            try{
                image.transferTo(new File(saveFile));
            }catch (Exception e){
                new IOException();
            }

            imageBoard.addImageData(
                                ImageData.builder()
                                        .imageName(saveName)
                                        .oldName(originalName)
                                        .imageStep(step++)
                                        .build()
                        );
        }
    }

    // 이미지 파일 삭제
    void deleteFilesProc(List<String> deleteFiles) {
        String filePath = FilePathProperties.FILE_PATH;

        for(int i = 0; i < deleteFiles.size(); i++){
            File file = new File(filePath + deleteFiles.get(i));

            if(file.exists())
                file.delete();
        }
    }

    @Override
    public ImageDetailDTO getModifyData(long imageNo, Principal principal) {
        ImageDetailDTO dto = imageBoardRepository.imageDetailDTO(imageNo);

        if(principal == null || !dto.getUserId().equals(principal.getName()))
            new AccessDeniedException("AccessDenied");

        return dto;
    }

    @Override
    public List<ImageDataDTO> getModifyImageAttach(long imageNo) {

        return imageDataRepository.getImageData(imageNo);
    }
}
