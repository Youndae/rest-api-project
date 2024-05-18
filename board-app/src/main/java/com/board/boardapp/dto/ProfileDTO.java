package com.board.boardapp.dto;

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

    public void setUserStatus(UserStatusDTO userStatus) {
        this.userStatus = userStatus;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }
}
