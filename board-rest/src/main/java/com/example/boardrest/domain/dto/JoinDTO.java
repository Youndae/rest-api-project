package com.example.boardrest.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@EqualsAndHashCode
public class JoinDTO {

    private String userId;

    private String userPw;

    private String userName;

    private String nickName;

    private String email;

    private MultipartFile profileThumbnail;

}
