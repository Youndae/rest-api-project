package com.example.boardrest.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class Count_table {

    @Id
    private String boardName;

    private long contentCount;
}
