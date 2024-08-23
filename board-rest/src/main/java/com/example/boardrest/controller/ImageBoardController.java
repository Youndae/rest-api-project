package com.example.boardrest.controller;

import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageBoardDetailDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageDataDTO;
import com.example.boardrest.domain.dto.iBoard.out.ImageModifyInfoDTO;
import com.example.boardrest.domain.dto.iBoard.in.ImageBoardRequestDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.factory.ResponseFactory;
import com.example.boardrest.domain.mapper.CriteriaRequestMapper;
import com.example.boardrest.service.ImageBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image-board")
@Slf4j
public class ImageBoardController {

    private final ImageBoardService imageBoardService;

    private final ResponseFactory responseFactory;

    @GetMapping("/")
    public ResponseEntity<ResponsePageableListDTO<ImageBoardDTO>> getList(@RequestParam(value = "pageNum") int pageNum
                                                            , @RequestParam(value = "keyword", required = false) String keyword
                                                            , @RequestParam(value = "searchType", required = false) String searchType
                                                            , Principal principal){

        Criteria cri = CriteriaRequestMapper.fromBoardRequest(pageNum, keyword, searchType);
        Page<ImageBoardDTO> dto = imageBoardService.getImageBoardList(cri);

        return responseFactory.createListResponse(dto, principal);
    }

    @GetMapping("/{imageNo}")
    public ResponseEntity<ResponseDetailAndModifyDTO<ImageBoardDetailDTO>> getDetail(@PathVariable long imageNo, Principal principal){

        ImageBoardDetailDTO dto = imageBoardService.getImageBoardDetail(imageNo);
        return responseFactory.createDetailResponse(dto, principal);
    }

    // 등록된 boardNo return
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public Long insertBoard(@RequestParam List<MultipartFile> files
                            , @ModelAttribute ImageBoardRequestDTO dto
                            , Principal principal) {

        return imageBoardService.imageInsertCheck(files, dto, principal);
    }


    @GetMapping("/patch-detail/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDetailAndModifyDTO<ImageModifyInfoDTO>> getPatchDetail(@PathVariable long imageNo, Principal principal) {

        ImageModifyInfoDTO dto = imageBoardService.getModifyData(imageNo, principal);

        return responseFactory.createDetailResponse(dto, principal);
    }

    @GetMapping("/patch-detail/image/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ImageDataDTO>> getPatchImageData(@PathVariable long imageNo){

        return new ResponseEntity<>(imageBoardService.getModifyImageAttach(imageNo), HttpStatus.OK);
    }

    @PatchMapping("/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public long patchBoard(@PathVariable long imageNo
                            , @RequestParam(value = "files", required = false) List<MultipartFile> files
                            , @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles
                            , @ModelAttribute ImageBoardRequestDTO dto
                            , Principal principal){


        return imageBoardService.imagePatchCheck(files, deleteFiles, imageNo, dto, principal);
    }

    @DeleteMapping("/{imageNo}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')")
    public String imageBoardDelete(@PathVariable long imageNo, Principal principal){

        return imageBoardService.deleteImageBoard(imageNo, principal);
    }

    @GetMapping("/display/{imageName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String imageName){

        return imageBoardService.getFile(imageName);
    }
}
