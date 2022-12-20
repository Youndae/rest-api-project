package com.example.boardrest.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    long imageInsertCheck(List<MultipartFile> images
            , HttpServletRequest request
            , Principal principal);

    long imageModifyCheck(List<MultipartFile> images
            , List<String> deleteFiles
            , HttpServletRequest request
            , Principal principal);

    long deleteImageBoard(long imageNo, HttpServletRequest request);
}
