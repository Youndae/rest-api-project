package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.ImageBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageBoardRepository extends JpaRepository<ImageBoard, Long>, ImageBoardRepositoryCustom {

}
