package com.example.boardrest.domain.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String userId;

    private String userPw;

    private String username;

    private String email;

    @Column(unique = true)
    private String nickName;

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

    public void addAuth(Auth auth) {
        auths.add(auth);
        auth.setMember(this);
    }


    public Member(Long id, @NonNull String userId, String userPw, String username, String email, String nickName, String profileThumbnail, @NonNull String provider) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.id = id;
        this.userId = userId;
        this.userPw = userPw == null ? null : passwordEncoder.encode(userPw);
        this.username = username;
        this.nickName = nickName;
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
        this.nickName = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileThumbnail(String profileThumbnail) {
        this.profileThumbnail = profileThumbnail;
    }
}
