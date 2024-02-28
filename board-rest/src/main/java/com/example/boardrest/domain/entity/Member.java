package com.example.boardrest.domain.entity;

import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class Member {

    @Id
    private String userId;

    private String userPw;

    private String userName;

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)


    /*@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final Set<ImageBoard> imageBoards = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private final Set<Comment> comments = new HashSet<>();*/

    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Auth> auths;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Member(String userId, String userPw, String userName, List<Auth> auths) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.userId = userId;
        this.userPw = passwordEncoder.encode(userPw);
        this.userName = userName;
        this.auths = auths;
    }
}
