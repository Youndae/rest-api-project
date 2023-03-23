package com.example.boardrest.service;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.ImageBoard;
import com.example.boardrest.domain.ImageData;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.properties.ImageSizeProperties;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageBoardServiceImpl implements ImageBoardService{

    private final PrincipalService principalService;

    private final ImageBoardRepository imageBoardRepository;

    private final ImageDataRepository imageDataRepository;

    private static long sizeSum = 0;

    @Override
    public ImageBoardDetailDTO getImageBoardDetail(long imageNo, Principal principal) {

        ImageDetailDTO detailDTO = imageBoardRepository.imageDetailDTO(imageNo);

        List<ImageDetailDataDTO> dataDTO = imageDataRepository.getImageData(imageNo);

        String uid = null;

        if(principal != null){
            uid = principal.getName();
        }

        ImageBoardDetailDTO dto = ImageBoardDetailDTO.builder()
                .imageNo(detailDTO.getImageNo())
                .imageTitle(detailDTO.getImageTitle())
                .userId(detailDTO.getUserId())
                .imageContent(detailDTO.getImageContent())
                .imageDate(detailDTO.getImageDate())
                .imageData(dataDTO)
                .uid(uid)
                .build();

        return dto;
    }

    @Override
    public Page<ImageBoardDTO> getImageBoardList(int pageNum, int amount, String keyword, String searchType) {

        log.info("pageNum : {}, amount : {}, keyword : {}, searchType : {}"
        , pageNum
        , amount
        , keyword
        , searchType);

        Page<ImageBoardDTO> dto;

        if(keyword != null)
            keyword = "%" + keyword + "%";

        if(keyword == null){//default
            dto = imageBoardRepository.getImageBoardList(
                    PageRequest.of(pageNum - 1
                            , amount
                            , Sort.by("imageNo").descending())
            );
        }else if(searchType.equals("t")){//제목 검색
            dto = imageBoardRepository.getImageBoardSearchTitle(
                    keyword
                    , PageRequest.of(pageNum - 1
                    , amount
                    , Sort.by("imageNo").descending())
            );
        }else if(searchType.equals("c")){//내용 검색
            dto = imageBoardRepository.getImageBoardSearchContent(
                    keyword
                    , PageRequest.of(pageNum - 1
                            , amount
                            , Sort.by("imageNo").descending())
            );
        }else if(searchType.equals("u")){//작성자 검색
            dto = imageBoardRepository.getImageBoardSearchWriter(
                    keyword
                    , PageRequest.of(pageNum - 1
                            , amount
                            , Sort.by("imageNo").descending())
            );
        }else if(searchType.equals("tc")){//제목 + 내용 검색
            dto = imageBoardRepository.getImageBoardSearchTitleAndContent(
                    keyword
                    , PageRequest.of(pageNum - 1
                            , amount
                            , Sort.by("imageNo").descending())
            );
        }else{
            dto = null;
        }


        return dto;
    }

    // 이미지파일 사이즈 체크
    @Override
    public long imageSizeCheck(List<MultipartFile> images) throws Exception {
        log.info("image size Check");

        for(MultipartFile image : images){
            sizeSum += image.getSize();

            if(sizeSum >= ImageSizeProperties.LIMIT_SIZE){
                log.info("image size is larger than the limit size");
                return ImageSizeProperties.RESULT_EXCEED_SIZE;
            }
        }

        log.info("image size check success");

        return ImageSizeProperties.RESULT_SUCCESS;
    }

    // 이미지 게시판 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long imageInsertCheck(List<MultipartFile> images
                                , String imageTitle
                                , String imageContent
                                , HttpServletRequest request
                                , Principal principal) {
        log.info("image insert check");

        try{
            if(imageSizeCheck(images) == ImageSizeProperties.RESULT_EXCEED_SIZE)
                return ImageSizeProperties.RESULT_EXCEED_SIZE;
        }catch (Exception e){
            log.info("size check Exception");
            return -1;
        }

        try{
            ImageBoard imageBoard = ImageBoard.builder()
                    .member(principalService.checkPrincipal(principal))
                    .imageTitle(request.getParameter("imageTitle"))
                    .imageContent(request.getParameter("imageContent"))
                    .imageDate(Date.valueOf(LocalDate.now()))
                    .build();

            imageInsert(images, request, 1, imageBoard);

            return imageBoardRepository.save(imageBoard).getImageNo();
        }catch (Exception e){
            log.info("insertion Exception!");
            return -1;
        }

    }

    // 이미지 게시판 patch
    @Override
    public long imagePatchCheck(List<MultipartFile> images
                        , List<String> deleteFiles
                        , HttpServletRequest request
                        , Principal principal) {
        log.info("image patch check");

        try{
            if(images != null){
                if(imageSizeCheck(images) == ImageSizeProperties.RESULT_EXCEED_SIZE)
                    return ImageSizeProperties.RESULT_EXCEED_SIZE;
            }
        }catch (Exception e){
            log.info("patch size check exception!");
            return -1;
        }

        long imageNo = Long.parseLong(request.getParameter("imageNo"));

        try{
            ImageBoard imageBoard = ImageBoard.builder()
                    .member(principalService.checkPrincipal(principal))
                    .imageNo(imageNo)
                    .imageTitle(request.getParameter("imageTitle"))
                    .imageContent(request.getParameter("imageContent"))
                    .imageDate(Date.valueOf(LocalDate.now()))
                    .build();

            if(images != null)
                imageInsert(images, request, imageDataRepository.countImageStep(imageNo) + 1, imageBoard);

            if(deleteFiles != null)
                deleteFilesProc(deleteFiles);

            return imageBoardRepository.save(imageBoard).getImageNo();
        }catch (Exception e){
            log.info("patch exception!");
            return -1;
        }
    }

    // 이미지 게시판 delete
    @Override
    public long deleteImageBoard(long imageNo, Principal principal) {
        log.info("delete imageBoard");

        Optional<ImageBoard> entity = imageBoardRepository.findById(imageNo);

        if(!entity.get().getMember().getUserId().equals(principal.getName()))
            return 0;

        try{
            List<String> deleteFileName = imageDataRepository.deleteImageDataList(imageNo);

            deleteFilesProc(deleteFileName);

            imageBoardRepository.deleteById(imageNo);

            return 1;
        }catch (Exception e){
            log.info("delete board exception!");
            return 0;
        }
    }

    // 이미지 파일 저장 및 imageData save 처리
    void imageInsert(List<MultipartFile> images
                            , HttpServletRequest request
                            , int step
                            , ImageBoard imageBoard) throws Exception{

        String filePath = "E:\\upload\\boardProject\\";

        for(MultipartFile image : images){
            String originalName = image.getOriginalFilename();

            try{
                StringBuffer sb = new StringBuffer();
                String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()))
                        .append(UUID.randomUUID().toString())
                        .append(originalName.substring(originalName.lastIndexOf("."))).toString();

                String saveFile = filePath + saveName;

                image.transferTo(new File(saveFile));

                log.info("saveName : " + saveName + ", originalName : " + originalName + ", imageStep : " + step);

                ImageData imageData = ImageData.builder()
                        .imageName(saveName)
                        .oldName(originalName)
                        .imageStep(step)
                        .build();

                imageBoard.addImageData(imageData);

                step++;
            }catch (Exception e){
                log.info("imageInsert exception");
            }
        }
    }

    // 이미지 파일 삭제
    void deleteFilesProc(List<String> deleteFiles) throws Exception{
        String filePath = "E:\\upload\\boardProject\\";

        try{
            for(int i = 0; i < deleteFiles.size(); i++){
                imageDataRepository.deleteById(deleteFiles.get(i));
                File file = new File(filePath + deleteFiles.get(i));

                if(file.exists())
                    file.delete();
            }
        }catch (Exception e){
            log.info("delete file Process Exception");
        }
    }

    @Override
    public ImageDetailDTO getModifyData(long imageNo, Principal principal) {

        if(principal == null){
            return null;
        }

        ImageDetailDTO dto = imageBoardRepository.imageDetailDTO(imageNo);

        if(!dto.getUserId().equals(principal.getName()))
            return null;

        return dto;
    }

    @Override
    public List<ImageDataDTO> getModifyImageAttach(long imageNo, Principal principal) {

        List<ImageDataDTO> dto = imageDataRepository.imageDataList(imageNo);

        return dto;
    }
}
