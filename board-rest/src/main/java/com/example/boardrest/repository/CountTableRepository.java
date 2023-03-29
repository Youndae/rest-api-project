package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.CountTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface CountTableRepository extends JpaRepository<CountTable, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE CountTable ct " +
            "SET ct.contentCount = ?1 " +
            "WHERE ct.boardName = ?2")
    void BoardCountUpdate(long contentCount, String boardName);


    @Query(value = "SELECT contentCount " +
            "FROM countTable " +
            "WHERE boardName = ?1"
    , nativeQuery = true)
    long boardCountValue(String boardName);
}
