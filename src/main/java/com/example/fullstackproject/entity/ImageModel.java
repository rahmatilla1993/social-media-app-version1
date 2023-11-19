package com.example.fullstackproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "image_model")
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "generated_name")
    private String generatedName;

    private long size;

    @OneToOne
    @JoinColumn(
            name = "post_id",
            referencedColumnName = "id"
    )
    private Post imagePost;
}
