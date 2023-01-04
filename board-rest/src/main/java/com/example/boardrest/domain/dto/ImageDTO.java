package com.example.boardrest.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {

    private long imageNo;

    private String imageName;

    private String imageContent;

    private Date imageDate;

    private String imageTitle;

    private String userId;

    private int imageStep;

    private String oldName;
}
