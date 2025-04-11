package com.fiitimprove.backend.models;

import java.nio.file.Paths;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {
    public static final String STORAGE_PATH = Paths.get("imagesStorage").toAbsolutePath().toString();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Image path can not be empty")
    @JoinColumn(name = "path", nullable = false)
    private String path;
}
