package com.example.boardrest.service;

import com.example.boardrest.customException.CustomIOException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.enumuration.SaveImageKey;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ImageFileServiceImpl implements ImageFileService{

    @Value("#{filePath['file.board.path']}")
    private String boardPath;

    @Value("#{filePath['file.profile.path']}")
    private String profilePath;

    private final int MAX_PIXEL = 5000;

    private final int SIZE_300 = 300;

    private final int SIZE_600 = 600;

    private final String EXTENSION = "jpg";

    @Override
    public String profileImageSave(MultipartFile file) throws IOException {
        validateResolution(file);
        String saveNamePrefix = createSaveFileName(file).get(SaveImageKey.SAVE_NAME);
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        return imageResizing(originalImage, saveNamePrefix, SIZE_300, profilePath);
    }

    @Override
    public Map<SaveImageKey, String> boardImageSave(MultipartFile file) throws IOException {
        validateResolution(file);
        Map<SaveImageKey, String> saveImageMap = createSaveFileName(file);
        String saveName = saveImageMap.get(SaveImageKey.SAVE_NAME);
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            Thumbnails.of(originalImage)
                    .scale(1.0)
                    .outputFormat(EXTENSION)
                    .outputQuality(0.9)
                    .toFile(new File(boardPath + saveName));

            imageResizing(originalImage, saveName, SIZE_300, boardPath);
            imageResizing(originalImage, saveName, SIZE_600, boardPath);

            return saveImageMap;
        }catch (Exception e) {
            if(!saveImageMap.isEmpty())
                cleanupImageBoardFiles(saveImageMap.get(SaveImageKey.SAVE_NAME));

            throw new CustomIOException(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private String imageResizing(BufferedImage file, String fileName, int size, String filePath) throws IOException {
        String saveName = createResizeName(fileName, size);

        File targetFile = new File(filePath + saveName);

        Thumbnails.of(file)
                .size(size, size)
                .outputFormat(EXTENSION)
                .outputQuality(0.8)
                .toFile(targetFile);

        return saveName;
    }

    private void validateResolution(MultipartFile file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.getInputStream())){
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if(readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);

                    if(width > MAX_PIXEL || height > MAX_PIXEL)
                        throw new IllegalArgumentException("이미지 해상도 너무 높음");
                }finally {
                    reader.dispose();
                }
            }else
                throw new IllegalArgumentException("지원하지 않는 이미지 형식");
        }
    }

    private Map<SaveImageKey, String> createSaveFileName(MultipartFile image) {
        Map<SaveImageKey, String> map = new HashMap<>();

        String originalName = image.getOriginalFilename();
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID())
                .append(".")
                .append(EXTENSION)
                .toString();

        map.put(SaveImageKey.SAVE_NAME, saveName);
        map.put(SaveImageKey.ORIGIN_NAME, originalName);

        return map;
    }

    private String createResizeName(String fileName, int size) {
        String saveNamePrefix = fileName.substring(0, fileName.lastIndexOf('.'));
        String saveName = saveNamePrefix + "_" + size + "." + EXTENSION;

        return saveName;
    }

    @Override
    public void cleanupImageBoardFiles(String saveName) {
        deleteFile(boardPath, saveName);
        String resized300Name = createResizeName(saveName, SIZE_300);
        String resized600Name = createResizeName(saveName, SIZE_600);
        deleteFile(boardPath, resized300Name);
        deleteFile(boardPath, resized600Name);
    }

    @Override
    public void deleteImageBoardFile(List<String> deleteFiles) {
        deleteFiles.forEach(this::cleanupImageBoardFiles);
    }

    @Override
    public void deleteFile(String filePath, String image) {

        File file = new File(filePath + image);

        if(file.exists())
            file.delete();
    }

    @Override
    public ResponseEntity<byte[]> getProfileImageDisplay(String imageName) {
        return getDisplayImage(profilePath, imageName);
    }

    @Override
    public ResponseEntity<byte[]> getBoardImageDisplay(String imageName) {
        return getDisplayImage(boardPath, imageName);
    }


    private ResponseEntity<byte[]> getDisplayImage(String filePath, String imageName) {
        File file = new File(filePath + imageName);
        ResponseEntity<byte[]> result = null;

        try {
            HttpHeaders header = new HttpHeaders();

            header.add("Content-Type", "image/jpeg");

            byte[] imageBytes = FileCopyUtils.copyToByteArray(file);
            result = new ResponseEntity<>(imageBytes, header, HttpStatus.OK);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
