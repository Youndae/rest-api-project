package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.dto.ImageDataDTO;
import com.example.boardrest.domain.dto.ImageDetailDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageDataRepository extends JpaRepository<ImageData, String> {

    @Query(value = "SELECT d.imageName AS imageName" +
            ", d.imageBoard.imageNo AS imageNo" +
            ", d.oldName AS oldName" +
            ", d.imageStep AS imageStep " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.imageNo = ?1 " +
            "ORDER BY d.imageStep ASC")
    List<ImageDataDTO> imageDataList(long imageNo);

    @Query(value = "SELECT imageName " +
            "FROM imageData " +
            "WHERE imageNo = ?1"
    , nativeQuery = true)
    List<String> getDeleteImageDataList(long imageNo);

    @Query(value = "SELECT MAX(d.imageStep) " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.imageNo = ?1")
    int countImageStep(long imageNo);

    @Query(value = "SELECT new com.example.boardrest.domain.dto.ImageDetailDataDTO(" +
            "id.imageName " +
            ", id.oldName " +
            ", id.imageStep) " +
            "FROM ImageData id " +
            "WHERE id.imageBoard.imageNo = ?1 " +
            "ORDER BY id.imageStep asc")
    List<ImageDetailDataDTO> getImageData(long imageNo);


    @Modifying
    @Query(value = "DELETE FROM ImageData id WHERE id.imageName in :deleteFileList")
    void deleteImageDataList(List<String> deleteFileList);

}
