package com.board.boardapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardDetailAndModifyDTO<T> {

    private T content;

    private UserStatusDTO userStatus;
}
