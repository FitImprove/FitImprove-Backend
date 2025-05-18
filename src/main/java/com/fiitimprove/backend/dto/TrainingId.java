package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for acception training id
 */
@Getter
@Setter
public class TrainingId {
    @NotNull
    private Long trainingId;
}