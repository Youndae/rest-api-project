package com.example.boardrest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageFileServiceImpl implements ImageFileService{

    @Override
    public Map<String, String> saveFile(String filePath, MultipartFile image) {

        Map<String, String> map = new HashMap<>();

        String originalName = image.getOriginalFilename();
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(originalName.substring(originalName.lastIndexOf(".")))
                .toString();
        String saveFile = filePath + saveName;

        try{
            image.transferTo(new File(saveFile));
        }catch (Exception e){
            new IOException();
        }

        map.put("imageName", saveName);
        map.put("oldName", originalName);


        return map;
    }

    @Override
    public void deleteFile(String filePath, String image) {

        File file = new File(filePath + image);

        if(file.exists())
            file.delete();
    }
}
