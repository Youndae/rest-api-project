package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.ImageData;
import com.example.boardrest.domain.dto.imageBoard.out.ImageDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageDataRepository extends JpaRepository<ImageData, String> {

    @Query(value = "SELECT d.imageName " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.id = ?1 " +
            "ORDER BY d.imageStep ASC")
    List<String> getImageDataNameList(long imageId);

    @Query(value = "SELECT MAX(d.imageStep) " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.id = ?1")
    int countImageStep(long imageNo);

    @Query(value = "SELECT new com.example.boardrest.domain.dto.imageBoard.out.ImageDataResponse(" +
                "d.imageName " +
                ", d.originName " +
                ", d.imageStep" +
            ") " +
            "FROM ImageData d " +
            "WHERE d.imageBoard.id = ?1 " +
            "ORDER BY d.imageStep asc")
    List<ImageDataResponse> getImageDataList(long id);


    @Modifying
    @Query(value = "DELETE FROM ImageData d WHERE d.imageName in :deleteFileList")
    void deleteImageDataList(@Param("deleteFileList") List<String> deleteFileList);


    @Modifying
    @Query(value = "DELETE FROM ImageData d WHERE d.imageBoard.id = ?1")
    void deleteImageDataListByImageId(long id);
}
