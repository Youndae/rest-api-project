package com.example.boardrest.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageFileService {

    Map<String, String> saveFile(String filePath, MultipartFile image);

    void deleteFile(String filePath, String image);
}
