package com.example.boardrest.domain;

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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentNo;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private Date commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    @ManyToOne
    @JoinColumn(name = "imageNo")
    private ImageBoard imageBoard;

    @ManyToOne
    @JoinColumn(name = "boardNo")
    private HierarchicalBoard hierarchicalBoard;
}
