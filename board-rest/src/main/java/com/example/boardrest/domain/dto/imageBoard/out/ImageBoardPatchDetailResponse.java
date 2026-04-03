package com.example.boardrest.domain.dto.imageBoard.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageBoardPatchDetailResponse {
    String title;
    String content;
    List<ImageDataResponse> imageList;
}
