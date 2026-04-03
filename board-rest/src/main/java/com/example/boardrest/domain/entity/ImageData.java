package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "image_data")
public class ImageData {

    @Id
    @Column(name = "image_name")
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageBoard imageBoard;


    @Column(nullable = false, name = "origin_name")
    private String originName;


    @Column(nullable = false, name = "image_step")
    private int imageStep;
}
