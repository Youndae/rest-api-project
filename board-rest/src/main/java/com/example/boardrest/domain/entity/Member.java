package com.example.boardrest.domain.entity;

import com.example.boardrest.auth.oAuth.domain.OAuth2DTO;
import com.example.boardrest.domain.enums.Role;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "user_id",
            length = 100,
            unique = true,
            nullable = false
    )
    private String userId;

    private String password;

    @Column(name = "user_name", nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(unique = true, length = 50)
    private String nickname;

    private String profile;

    @Column(nullable = false)
    private String provider;

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Auth> auths = new ArrayList<>();

    public void addAuth() {
        Auth auth = Auth.builder()
                        .auth(Role.MEMBER.getKey())
                        .build();
        auths.add(auth);
        auth.setMember(this);
    }

    public void updatePassword(String rawPassword, BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfileData(String profile, String nickname, String email) {
        updateNickname(nickname);
        updateProfile(profile);
        updateEmail(email);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }


    public OAuth2DTO toOAuth2DTOUseFilter() {
        return OAuth2DTO.builder()
                .userId(this.userId)
                .username(this.username)
                .authList(this.auths)
                .build();
    }
}
