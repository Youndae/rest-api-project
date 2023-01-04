package com.example.boardrest.domain;

import com.example.boardrest.domain.Comment;
import com.example.boardrest.domain.ImageData;
import com.example.boardrest.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageNo;

    @NonNull
    private String imageTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Member member;

    private Date imageDate;

    private String imageContent;

    @OneToMany(mappedBy = "imageBoard", fetch = FetchType.LAZY)
    private final Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "imageBoard", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<ImageData> imageDataSet = new HashSet<>();

    public void addImageData(ImageData imageData){
        imageDataSet.add(imageData);
        imageData.setImageBoard(this);
    }
}
