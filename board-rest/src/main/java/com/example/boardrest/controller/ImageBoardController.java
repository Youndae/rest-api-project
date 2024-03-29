package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.properties.FilePathProperties;
import com.example.boardrest.service.ImageBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image-board")
@Slf4j
public class ImageBoardController {

    private final ImageBoardService imageBoardService;

    @GetMapping("/image-board-list")
    public ResponseEntity<ResponsePageableListDTO<ImageBoardDTO>> imageBoardList(@RequestParam(value = "pageNum") int pageNum
                                                            , @RequestParam(value = "keyword", required = false) String keyword
                                                            , @RequestParam(value = "searchType", required = false) String searchType
                                                            , Principal principal){

        Criteria cri = Criteria.builder()
                                .pageNum(pageNum)
                                .keyword(keyword)
                                .searchType(searchType)
                                .build();

        return new ResponseEntity<>(imageBoardService.getImageBoardList(cri, principal), HttpStatus.OK);
    }

    @GetMapping("/image-board-detail/{imageNo}")
    public ResponseEntity<ResponseDetailAndModifyDTO<ImageBoardDetailDTO>> imageBoardDetail(@PathVariable long imageNo, Principal principal){

        return new ResponseEntity<>(imageBoardService.getImageBoardDetail(imageNo, principal), HttpStatus.OK);
    }


    @GetMapping("/modify-data/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<ImageModifyInfoDTO>> imageBoardModifyInfo(@PathVariable long imageNo, Principal principal) {

        return new ResponseEntity<>(imageBoardService.getModifyData(imageNo, principal), HttpStatus.OK);
    }

    @GetMapping("/modify-image-attach/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ImageDataDTO>> modifyImageAttach(@PathVariable long imageNo){

        return new ResponseEntity<>(imageBoardService.getModifyImageAttach(imageNo), HttpStatus.OK);
    }

    // 등록된 boardNo return
    @PostMapping("/image-insert")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long imageBoardInsert(@RequestParam List<MultipartFile> files
                                , @RequestParam String imageTitle
                                , @RequestParam String imageContent
                                , HttpServletRequest request
                                , Principal principal) {

        return imageBoardService.imageInsertCheck(files, imageTitle, imageContent, request, principal);
    }


    @PatchMapping("/image-modify")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long imageBoardPatch(@RequestParam(value = "files", required = false) List<MultipartFile> files
                                , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                , HttpServletRequest request
                                , Principal principal){

        return imageBoardService.imagePatchCheck(files, deleteFiles, request, principal);
    }

    @DeleteMapping("/image-delete/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long imageBoardDelete(@PathVariable long imageNo, Principal principal){

        return imageBoardService.deleteImageBoard(imageNo, principal);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(@RequestParam(value = "imageName") String imageName){

        File file = new File(FilePathProperties.FILE_PATH + imageName);
        ResponseEntity<byte[]> result = null;

        try{
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
