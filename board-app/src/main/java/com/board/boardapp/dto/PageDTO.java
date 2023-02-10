package com.board.boardapp.dto;

import lombok.*;

@Getter
@ToString
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    private boolean last;

    private long totalPages;

    private long totalElements;

    private long number;

    private int size;

    private int numberOfElements;

    private boolean first;

    private boolean empty;
}
