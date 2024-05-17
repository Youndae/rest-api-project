package com.example.boardrest.service;

import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import com.example.boardrest.repository.MemberRepository;
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
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
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
    public ResponseDetailAndModifyDTO<ImageBoardDetailDTO> getImageBoardDetail(long imageNo, Principal principal) {
        ImageBoard imageBoard = imageBoardRepository
                                    .findById(imageNo)
                                    .orElseThrow(() -> new NullPointerException("nullPointerException"));

        List<ImageDataDTO> dataDTO = imageDataRepository.getImageData(imageNo);

        ImageBoardDetailDTO dto = ImageBoardDetailDTO.builder()
                                                .imageNo(imageBoard.getImageNo())
                                                .imageTitle(imageBoard.getImageTitle())
                                                .nickname(imageBoard.getMember().getNickname())
                                                .imageContent(imageBoard.getImageContent())
                                                .imageDate(imageBoard.getImageDate())
                                                .imageData(dataDTO)
                                                .build();

        ResponseDetailAndModifyDTO<ImageBoardDetailDTO> responseDTO = new ResponseDetailAndModifyDTO<>(dto, principalService.getNicknameToPrincipal(principal));

        return responseDTO;
    }

    @Override
    public ResponsePageableListDTO<ImageBoardDTO> getImageBoardList(Criteria cri, Principal principal) {
        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
            , cri.getImageAmount()
                , Sort.by("imageNo").descending()
        );

        Page<ImageBoardDTO> dto = imageBoardRepository.findAll(cri, pageable);
        ResponsePageableListDTO<ImageBoardDTO> responseDTO = new ResponsePageableListDTO<>(dto, principalService.getNicknameToPrincipal(principal));

        return responseDTO;
    }

    // 이미지 게시판 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long imageInsertCheck(List<MultipartFile> images
                                , String imageTitle
                                , String imageContent
                                , HttpServletRequest request
                                , Principal principal) {

        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        ImageBoard imageBoard = ImageBoard.builder()
                .member(memberEntity)
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
                                , long imageNo
                                , HttpServletRequest request
                                , Principal principal) {

        ImageBoard imageBoard = imageBoardRepository
                .findById(imageNo)
                .orElseThrow(() -> new NullPointerException("NullPointerException"));

        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        String writer = imageBoard.getMember().getUserId();

        if(!writer.equals(principal.getName()))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, "AccessDenied");

        imageBoard = ImageBoard.builder()
                                .member(memberEntity)
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
    private void imageInsert(List<MultipartFile> images
                            , int step
                            , ImageBoard imageBoard) {
        String filePath = FilePathProperties.BOARD_FILE_PATH;
        Map<String, String> map;
        for(MultipartFile image : images){
            map = imageFileService.saveFile(filePath, image);

            imageBoard.addImageData(ImageData.builder()
                                    .imageName(map.get("imageName"))
                                    .oldName(map.get("oldName"))
                                    .imageStep(step++)
                                    .build()
                            );
        }

    }

    // 이미지 파일 삭제
    public void deleteFilesProc(List<String> deleteFiles) {
        String filePath = FilePathProperties.BOARD_FILE_PATH;

        deleteFiles.forEach(v -> imageFileService.deleteFile(filePath, v));

    }

    @Override
    public ResponseDetailAndModifyDTO<ImageModifyInfoDTO> getModifyData(long imageNo, Principal principal) {
        ImageModifyInfoDTO dto = imageBoardRepository.imageDetailDTO(imageNo);

        PrincipalDTO principalDTO = principalService.checkPrincipal(principal);

        if(!dto.getNickname().equals(principalDTO.getNickname()))
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());

        ResponseDetailAndModifyDTO<ImageModifyInfoDTO> responseDTO = new ResponseDetailAndModifyDTO<>(dto, principalService.getNicknameToPrincipal(principal));

        return responseDTO;
    }

    @Override
    public List<ImageDataDTO> getModifyImageAttach(long imageNo) {

        return imageDataRepository.getImageData(imageNo);
    }
}
