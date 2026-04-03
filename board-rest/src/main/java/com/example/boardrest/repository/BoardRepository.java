package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {


    @Modifying
    @Query(value = "DELETE FROM Board h " +
            "WHERE h.groupNo = ?1")
    void deleteByBoardGroupNo(long boardGroupNo);

}
