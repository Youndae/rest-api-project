package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.dto.iBoard.out.ImageDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageDataRepository extends JpaRepository<ImageData, String> {

    List<ImageData> findAllByImageBoard_ImageNoOrderByImageStepAsc(long imageNo);

    @Query(value = "SELECT d.imageName " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.imageNo = ?1")
    List<String> getDeleteImageDataList(long imageNo);

    @Query(value = "SELECT MAX(d.imageStep) " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.imageNo = ?1")
    int countImageStep(long imageNo);

    @Query(value = "SELECT new com.example.boardrest.domain.dto.iBoard.out.ImageDataDTO(" +
                "d.imageName " +
                ", d.oldName " +
                ", d.imageStep" +
            ") " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.imageNo = ?1 " +
            "ORDER BY d.imageStep asc")
    List<ImageDataDTO> getImageData(long imageNo);


    @Modifying
    @Query(value = "DELETE FROM ImageData d WHERE d.imageName in :deleteFileList")
    void deleteImageDataList(@Param("deleteFileList") List<String> deleteFileList);

}
