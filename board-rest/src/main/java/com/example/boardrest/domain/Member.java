package com.example.boardrest.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Auth> auths;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
