package com.example.boardrest.service;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    ResponsePageableListDTO<ImageBoardDTO> getImageBoardList(Criteria cri, Principal principal);

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

    ResponseDetailAndModifyDTO<ImageBoardDetailDTO> getImageBoardDetail(long imageNo, Principal principal);

    ResponseDetailAndModifyDTO<ImageModifyInfoDTO> getModifyData(long imageNo, Principal principal);

    List<ImageDataDTO> getModifyImageAttach(long imageNo);
}
