package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long authNo;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member member;*/

    private String userId;

    private String auth;
}
