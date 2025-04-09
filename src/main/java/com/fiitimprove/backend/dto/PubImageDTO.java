package com.fiitimprove.backend.dto;

import com.fiitimprove.backend.models.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubImageDTO {
    private Long userId;
    private Long id;
    private String path;
    public PubImageDTO(Long id, Long userId, String path) {
        this.id = id;
        this.userId = userId;
        this.path = path;
    }

    public static PubImageDTO create(Image img) {
        return new PubImageDTO(img.getId(), img.getUser().getId(), img.getPath());
    }
}
