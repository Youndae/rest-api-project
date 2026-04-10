package com.example.boardrest.domain.dto.member.out;

import com.example.boardrest.domain.enums.MailSuffix;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private String nickname;

    private String mailPrefix;

    private String mailSuffix;

    private String mailType;

    private String profile;

    public ProfileResponse (String nickname, String email, String profile) {
        String[] splitMail = email.split("@");
        String suffix = splitMail[1].substring(0, splitMail[1].indexOf('.'));
        String type = MailSuffix.findSuffixType(suffix);

        this.nickname = nickname;
        this.mailPrefix = splitMail[0];
        this.mailSuffix = splitMail[1];
        this.mailType = type;
        this.profile = profile;
    }
}
