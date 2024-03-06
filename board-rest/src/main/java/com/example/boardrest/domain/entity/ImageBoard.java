package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageNo;

    @NonNull
    private String imageTitle;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private Date imageDate;

    private String imageContent;

    @OneToMany(mappedBy = "imageBoard", cascade = CascadeType.ALL)
    private final Set<ImageData> imageDataSet = new HashSet<>();

    public void setImageNo(long imageNo) {
        this.imageNo = imageNo;
    }

    public void addImageData(ImageData imageData){
        imageDataSet.add(imageData);
        imageData.setImageBoard(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageBoard that = (ImageBoard) o;
        return Objects.equals(imageNo, that.imageNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageNo);
    }
}
