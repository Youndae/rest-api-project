package com.board.boardapp.domain.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProfileDTO {

    private String nickname;

    private String profileThumbnail;

    private UserStatusDTO userStatus;

    private String btnText;

    public void setStatusAndBtn() {
        this.userStatus = new UserStatusDTO(true, this.nickname);
        this.btnText = this.nickname == null ? "등록" : "수정";
    }
}
