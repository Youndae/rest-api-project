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

    @OneToMany(mappedBy = "member")
    private final Set<HierarchicalBoard> hierarchicalBoards = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private final Set<ImageBoard> imageBoards = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private final Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Auth> auths;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
