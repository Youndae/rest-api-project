package com.example.boardrest.service;

import com.example.boardrest.domain.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    Page<ImageBoardDTO> getImageBoardList(Criteria cri);

    long imageSizeCheck(List<MultipartFile> images) throws Exception;

    long imageInsertCheck(List<MultipartFile> images
                            , String imageTitle
                            , String imageContent
                            , HttpServletRequest request
                            , Principal principal);

    long imagePatchCheck(List<MultipartFile> images
            , List<String> deleteFiles
            , HttpServletRequest request
            , Principal principal);

    long deleteImageBoard(long imageNo, Principal principal);

    ImageBoardDetailDTO getImageBoardDetail(long imageNo, Principal principal);

    ImageDetailDTO getModifyData(long imageNo, Principal principal);

    List<ImageDataDTO> getModifyImageAttach(long imageNo, Principal principal);
}
