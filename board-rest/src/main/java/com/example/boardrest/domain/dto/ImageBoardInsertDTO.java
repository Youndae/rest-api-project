package com.example.boardrest.domain.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ImageBoardInsertDTO {

    private String imageTitle;

    private String imageContent;

    private List<MultipartFile> images;
}
