package com.example.boardrest.controller;

import com.example.boardrest.domain.ImageBoard;
import com.example.boardrest.domain.dto.ImageDTO;
import com.example.boardrest.domain.dto.ImageDataDTO;
import com.example.boardrest.domain.dto.ImageDetailDTO;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import com.example.boardrest.service.ImageBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imageBoard")
@Slf4j
public class ImageBoardController {

    private final ImageBoardService imageBoardService;

    private final ImageBoardRepository imageBoardRepository;

    private final ImageDataRepository imageDataRepository;

    @GetMapping("/image-board-list")
    public ResponseEntity<List<ImageDTO>> imageBoardList(){

        return new ResponseEntity<>(imageBoardRepository.imageBoardList(), HttpStatus.OK);
    }

    /**
     * imageBoardModify를 실행했을 경우 save만으로 수정을 처리하고 있는지 확인 필요.
     */
    @GetMapping("/image-board-detail/{imageNo}")
    public ResponseEntity<ImageDetailDTO> imageBoardDetail(@PathVariable long imageNo){

        return new ResponseEntity<>(imageBoardRepository.imageDetailDTO(imageNo), HttpStatus.OK);
    }


    @GetMapping("/image-board-modify/{imageNo}")
    public ResponseEntity<ImageDetailDTO> imageBoardModifyInfo(@PathVariable long imageNo){

        return new ResponseEntity<>(imageBoardRepository.imageDetailDTO(imageNo), HttpStatus.OK);
    }

    @GetMapping("/detail-image-list/{imageNo}")
    public ResponseEntity<List<ImageDataDTO>> detailImageList(@PathVariable long imageNo){
        log.info("detail ImageList");
        log.info("imageNo : " + imageNo);

        return new ResponseEntity<>(imageDataRepository.imageDataList(imageNo), HttpStatus.OK);
    }

    // 등록된 boardNo return
    @PostMapping("/image-insert")
    public long imageBoardInsert(@RequestParam("files")List<MultipartFile> images
                                    , HttpServletRequest request
                                    , Principal principal){
        log.info("image Insert");

        return imageBoardService.imageInsertCheck(images, request, principal);
    }

    @PatchMapping("/image-modify")
    public void imageBoardPatch(@RequestParam(value = "files", required = false) List<MultipartFile> images
                                    , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                    , HttpServletRequest request
                                    , Principal principal){
        log.info("image modify");

        imageBoardService.imagePatchCheck(images, deleteFiles, request, principal);
    }

    @DeleteMapping("/image-delete/{imageNo}")
    public void imageBoardDelete(@PathVariable long imageNo, HttpServletRequest request){
        log.info("delete imageBoard");

        imageBoardService.deleteImageBoard(imageNo, request);
    }
}
