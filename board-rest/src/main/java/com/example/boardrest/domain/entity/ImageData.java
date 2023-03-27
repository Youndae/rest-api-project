package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageData {

    @Id
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "imageNo")
    private ImageBoard imageBoard;

    @NonNull
    private String oldName;

    private int imageStep;
}
