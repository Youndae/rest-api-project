package com.example.boardrest.domain;

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
    private long authNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member member;

    private String auth;
}
