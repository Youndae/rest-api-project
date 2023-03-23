package com.example.boardrest.service;

import com.example.boardrest.domain.Criteria;
import com.example.boardrest.domain.dto.ImageBoardDTO;
import com.example.boardrest.domain.dto.ImageBoardDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    Page<ImageBoardDTO> getImageBoardList(int pageNum, int amount, String keyword, String searchType);

    long imageSizeCheck(MultipartFile[] images) throws Exception;

    long imageInsertCheck(MultipartFile[] images
                            , String imageTitle
                            , String imageContent
                            , HttpServletRequest request
                            , Principal principal);

    long imagePatchCheck(MultipartFile[] images
            , List<String> deleteFiles
            , HttpServletRequest request
            , Principal principal);

    long deleteImageBoard(long imageNo, HttpServletRequest request);

    ImageBoardDetailDTO getImageBoardDetail(long imageNo, Principal principal);
}
