package com.example.boardrest.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ImageFileService {

    Map<String, String> saveFile(String filePath, MultipartFile image) throws IOException;

    void deleteFile(String filePath, String image);

    void deleteImageBoardFile(List<String> deleteFiles);
}
