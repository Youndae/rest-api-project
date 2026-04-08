package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.common.in.ListRequest;
import com.example.boardrest.domain.dto.imageBoard.out.*;
import com.example.boardrest.domain.dto.imageBoard.in.ImageBoardRequest;
import com.example.boardrest.domain.dto.response.ApiResponse;
import com.example.boardrest.domain.dto.response.PageResponse;
import com.example.boardrest.service.ImageBoardService;
import com.example.boardrest.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image-board")
@Slf4j
public class ImageBoardController {

    private final ImageBoardService imageBoardService;

    private final ImageFileService imageFileService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<ImageBoardListResponse>>> getList(ListRequest request){

        request.validate();
        PageResponse<ImageBoardListResponse> dto = imageBoardService.getImageBoardList(request);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImageBoardDetailResponse>> getDetail(@PathVariable(name = "id") @Min(value = 1) long id){

        ImageBoardDetailResponse dto = imageBoardService.getImageBoardDetail(id);

        return ResponseEntity.ok(ApiResponse.success(dto));
    }


    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> insertBoard(@RequestParam List<MultipartFile> files,
                                                        @ModelAttribute @Valid ImageBoardRequest dto,
                                                        Principal principal
    ) {

        Long result = imageBoardService.imageBoardInsert(files, dto, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result));
    }


    @GetMapping("/patch/detail/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ImageBoardPatchDetailResponse>> getPatchDetail(
                            @PathVariable(name = "id") @Min(value = 1) long id,
                            Principal principal
    ) {

        ImageBoardPatchDetailResponse dto = imageBoardService.getPatchData(id, principal.getName());

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> patchBoard(
                            @PathVariable(name = "id") @Min(value = 1) long id,
                            @RequestParam(value = "files", required = false) List<MultipartFile> files,
                            @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles,
                            @ModelAttribute @Valid ImageBoardRequest request,
                            Principal principal
    ){

        Long result = imageBoardService.imageBoardPatch(files, deleteFiles, id, request, principal.getName());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> imageBoardDelete(
                            @PathVariable(name = "id") @Min(value = 1) long id,
                            Principal principal
    ){

        imageBoardService.deleteImageBoard(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getFile(@PathVariable(name = "imageName") String imageName) {

        return imageFileService.getBoardImageDisplay(imageName);
    }
}
