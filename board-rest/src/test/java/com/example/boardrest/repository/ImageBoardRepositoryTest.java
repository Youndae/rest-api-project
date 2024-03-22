package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.service.ImageBoardService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class ImageBoardRepositoryTest {

    @Autowired
    private ImageBoardRepository repository;

    @Autowired
    private ImageDataRepository imageDataRepository;

    @Autowired
    private ImageBoardService imageBoardService;

    @Test
    void imageListDTOTest() {

        Criteria cri = new Criteria();

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                , cri.getImageAmount()
                , Sort.by("imageNo").descending()
        );

        Page<ImageBoardDTO> dto = repository.findAll(cri, pageable);
    }

    @Test
    void imageDataListTest() {
        List<ImageData> dataList = imageDataRepository.findAllByImageBoard_ImageNoOrderByImageStepAsc(56L);

        System.out.println(dataList);
    }

    @Test
    public void imageDataTest(){
        long imageNo = 10;

        ImageModifyInfoDTO imageDTO = repository.imageDetailDTO(imageNo);

        List<ImageDataDTO> imageDataList = imageDataRepository.getImageData(imageNo);

        ImageBoardDetailDTO dto = ImageBoardDetailDTO.builder()
                .imageNo(imageDTO.getImageNo())
                .userId(imageDTO.getUserId())
                .imageContent(imageDTO.getImageContent())
                .imageDate(imageDTO.getImageDate())
                .imageTitle(imageDTO.getImageTitle())
                .imageData(imageDataList)
                .build();

        System.out.println(dto);
    }


    @Test
    void saveData() throws IOException {

        File file = new File("E:\\upload\\boardProject\\20161203_100400_IMG_0802.jpg");

        String filePath = "E:\\upload\\boardProject\\";

        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try{
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);

        }catch (Exception e){
            System.out.println("cast Exception");
            e.printStackTrace();
        }

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        String originalName = multipartFile.getOriginalFilename();

        for(int i = 62; i < 274; i++){

            try{
                StringBuffer sb = new StringBuffer();
                String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()))
                        .append(UUID.randomUUID().toString())
                        .append(originalName.substring(originalName.lastIndexOf("."))).toString();

                String saveFile = filePath + saveName;

                multipartFile.transferTo(new File(saveFile));


                imageDataRepository.save(
                        ImageData.builder()
                                .imageName(saveName)
                                .imageBoard(ImageBoard.builder().imageNo(i).imageTitle("testInsert" + i).build())
                                .oldName(originalName)
                                .imageStep(1)
                                .build()
                );
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        /*imageDataRepository.imageDataTestInsert(
                "saveName"
                , 1
                , "originalName"
                , 1
        );*/

    }



}