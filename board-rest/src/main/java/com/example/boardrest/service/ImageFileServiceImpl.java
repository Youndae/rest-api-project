package com.example.boardrest.service;

import com.example.boardrest.customException.CustomIOException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.properties.FilePathProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageFileServiceImpl implements ImageFileService{

    @Override
    public Map<String, String> saveFile(String filePath, MultipartFile image) throws IOException{

        Map<String, String> map = new HashMap<>();

        String originalName = image.getOriginalFilename();
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                            .format(System.currentTimeMillis()))
                            .append(UUID.randomUUID())
                            .append(originalName.substring(originalName.lastIndexOf(".")))
                            .toString();
        String saveFile = filePath + saveName;

        image.transferTo(new File(saveFile));


        map.put("imageName", saveName);
        map.put("oldName", originalName);


        return map;
    }

    @Override
    public void deleteImageBoardFile(List<String> deleteFiles) {
        String boardFilePath = FilePathProperties.BOARD_FILE_PATH;

        deleteFiles.forEach(v -> deleteFile(boardFilePath, v));
    }

    @Override
    public void deleteFile(String filePath, String image) {

        File file = new File(filePath + image);

        if(file.exists())
            file.delete();
    }
}
