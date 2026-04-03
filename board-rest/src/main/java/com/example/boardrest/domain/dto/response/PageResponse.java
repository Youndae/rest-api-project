package com.example.boardrest.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageResponse <T>{
    private List<T> items;
    private int totalPages;
    private boolean isEmpty;
    private int currentPage;

    public static<T> PageResponse<T> of(Page<T> content) {
        return PageResponse.<T>builder()
                .items(content.getContent())
                .totalPages(content.getTotalPages())
                .isEmpty(content.isEmpty())
                .currentPage(content.getNumber())
                .build();
    }
}
