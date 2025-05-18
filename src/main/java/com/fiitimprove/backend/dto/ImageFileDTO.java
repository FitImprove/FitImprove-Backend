package com.fiitimprove.backend.dto;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning all user's immages with id and data
 */
@Getter
@Setter
public class ImageFileDTO {
    private Long id;
    private byte[] data;
    public ImageFileDTO(Long id, byte[] data) {
        this.id = id;
        this.data = data;
    }
}
