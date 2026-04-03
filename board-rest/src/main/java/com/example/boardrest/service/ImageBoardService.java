package com.example.boardrest.service;

import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.imageBoard.out.*;
import com.example.boardrest.domain.dto.imageBoard.in.ImageBoardRequest;
import com.example.boardrest.domain.dto.response.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface ImageBoardService {

    PageResponse<ImageBoardListResponse> getImageBoardList(ListRequest request);

    ImageBoardDetailResponse getImageBoardDetail(long imageNo);

    long imageBoardInsert(List<MultipartFile> images, ImageBoardRequest dto, String userId);

    ImageBoardPatchDetailResponse getPatchData(long id, String userId);

    long imageBoardPatch(List<MultipartFile> images,
                        List<String> deleteFiles,
                        long id,
                        ImageBoardRequest dto,
                        String userId);

    void deleteImageBoard(long id, String userId);
}
