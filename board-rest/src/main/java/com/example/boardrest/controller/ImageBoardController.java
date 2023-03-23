package com.example.boardrest.controller;

import com.example.boardrest.domain.ImageBoard;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.repository.ImageBoardRepository;
import com.example.boardrest.repository.ImageDataRepository;
import com.example.boardrest.service.ImageBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ImageBoardRepository imageBoardRepository;

    private final ImageDataRepository imageDataRepository;

    @GetMapping("/image-board-list")
    public ResponseEntity<Page<ImageBoardDTO>> imageBoardList(@RequestParam(value = "pageNum") int pageNum
                                                            , @RequestParam(value = "amount") int amount
                                                            , @RequestParam(value = "keyword", required = false) String keyword
                                                            , @RequestParam(value = "searchType", required = false) String searchType){

        return new ResponseEntity<>(imageBoardService.getImageBoardList(pageNum, amount, keyword, searchType), HttpStatus.OK);
    }

    /**
     * imageBoardModify를 실행했을 경우 save만으로 수정을 처리하고 있는지 확인 필요.
     */
    @GetMapping("/image-board-detail/{imageNo}")
    public ResponseEntity<ImageBoardDetailDTO> imageBoardDetail(@PathVariable long imageNo, Principal principal){



        return new ResponseEntity<>(imageBoardService.getImageBoardDetail(imageNo, principal), HttpStatus.OK);
    }


    @GetMapping("/image-board-modify/{imageNo}")
    public ResponseEntity<ImageDetailDTO> imageBoardModifyInfo(@PathVariable long imageNo) {

        return new ResponseEntity<>(imageBoardRepository.imageDetailDTO(imageNo), HttpStatus.OK);
    }

    // 등록된 boardNo return
    @PostMapping("/image-insert")
    public long imageBoardInsert(@RequestParam MultipartFile[] files
                                , @RequestParam String imageTitle
                                , @RequestParam String imageContent
                                    , HttpServletRequest request
                                    , Principal principal){
        log.info("image Insert");

//        return imageBoardService.imageInsertCheck(images, request, principal);

        /*log.info("dto title : {}", dto.getImageTitle());
        log.info("dto content : {}", dto.getImageContent());
        log.info("dto images : {}", dto.getImages().get(0).getOriginalFilename());*/

        log.info("file length : {}", files.length);
        log.info("title : {}", imageTitle);
        log.info("content : {}", imageContent);

        for(MultipartFile multipartFile : files){
            log.info("origin name : {}",multipartFile.getOriginalFilename());
        }



//        return 1;

        return imageBoardService.imageInsertCheck(files, imageTitle, imageContent, request, principal);
    }

    @PatchMapping("/image-modify")
    public void imageBoardPatch(@RequestParam(value = "files", required = false) List<MultipartFile> images
                                    , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                                    , HttpServletRequest request
                                    , Principal principal){
        log.info("image modify");

//        imageBoardService.imagePatchCheck(images, deleteFiles, request, principal);
    }

    @DeleteMapping("/image-delete/{imageNo}")
    public void imageBoardDelete(@PathVariable long imageNo, HttpServletRequest request){
        log.info("delete imageBoard");

        imageBoardService.deleteImageBoard(imageNo, request);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(@RequestParam(value = "imageName") String imageName){

        log.info("imageController display imageName : {}", imageName);

        File file = new File("E:\\upload\\boardProject\\" + imageName);

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
