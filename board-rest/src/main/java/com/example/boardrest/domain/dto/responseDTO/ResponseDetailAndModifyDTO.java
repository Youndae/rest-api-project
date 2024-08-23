package com.example.boardrest.domain.dto.responseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @param <T>
 *     HierarchicalBoardModifyDTO
 *     HierarchicalBoardDetailDTO
 *     HierarchicalBoardReplyInfoDTO
 *     ImageBoardDetailDTO
 *     ImageModifyInfoDTO - modify
 *
 *
 * 목적
 *      ResponsePageableListDTO와 마찬가지로 정형화된 틀로 userStatus와 함께 보내주기 위함.
 *
 */

@Getter
@ToString
@NoArgsConstructor
public class ResponseDetailAndModifyDTO<T> {

    // detail data DTO
    private T content;

    //userStatus
    private UserStatusDTO userStatus;

    public ResponseDetailAndModifyDTO(T responseContent, String nickname){
        this.content = responseContent;
        this.userStatus = new UserStatusDTO(nickname);
    }
}
