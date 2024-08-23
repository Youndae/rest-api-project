package com.example.boardrest.domain.entity;

import com.example.boardrest.auth.oAuth.domain.OAuth2DTO;
import com.example.boardrest.domain.enumuration.Role;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class Member {

    @Id
    private String userId;

    private String userPw;

    private String username;

    private String email;

    @Column(unique = true)
    private String nickname;

    private String profileThumbnail;

    @NonNull
    private String provider;

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)


    /*@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final Set<ImageBoard> imageBoards = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private final Set<Comment> comments = new HashSet<>();*/

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Auth> auths = new ArrayList<>();

    public void addAuth() {
        Auth auth = Auth.builder()
                        .auth(Role.MEMBER.getKey())
                        .build();
        auths.add(auth);
        auth.setMember(this);
    }


    public Member(@NonNull String userId, String userPw, String username, String email, String nickname, String profileThumbnail, @NonNull String provider) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.userId = userId;
        this.userPw = userPw == null ? null : passwordEncoder.encode(userPw);
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.profileThumbnail = profileThumbnail;
        this.provider = provider;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileThumbnail(String profileThumbnail) {
        this.profileThumbnail = profileThumbnail;
    }

    public OAuth2DTO toOAuth2DTOUseFilter() {
        return OAuth2DTO.builder()
                .userId(this.userId)
                .username(this.username)
                .authList(this.auths)
                .build();
    }
}
