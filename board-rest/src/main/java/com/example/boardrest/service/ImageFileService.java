package com.example.boardrest.service;

import com.example.boardrest.domain.enumuration.SaveImageKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ImageFileService {

//    Map<String, String> saveFile(String filePath, MultipartFile image) throws IOException;

    String profileImageSave(MultipartFile file) throws IOException;

    Map<SaveImageKey, String> boardImageSave(MultipartFile file) throws IOException;

    void cleanupImageBoardFiles(String saveName);

    void deleteFile(String filePath, String image);

    void deleteImageBoardFile(List<String> deleteFiles);

    ResponseEntity<byte[]> getProfileImageDisplay(String imageName);

    ResponseEntity<byte[]> getBoardImageDisplay(String imageName);
}
