package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchicalBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardNo;

    private String boardTitle;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private String boardContent;

    private Date boardDate;

    private long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

    /*@OneToMany(mappedBy = "hierarchicalBoard")
    private final Set<Comment> comments = new HashSet<>();*/



}
