package com.example.boardrest.domain.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class JoinDTO {

    private String userId;

    private String userPw;

    private String userName;

    private String nickname;

    private String email;

}
