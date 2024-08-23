package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long authNo;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private String auth;

    public void setMember(Member member) {
        this.member = member;
    }
}
