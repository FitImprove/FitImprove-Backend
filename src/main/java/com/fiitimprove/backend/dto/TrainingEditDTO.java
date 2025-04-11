package com.fiitimprove.backend.dto;

import com.fiitimprove.backend.models.Training;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingEditDTO {
    @NotNull
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private int freeSlots;
    @NotNull
    private Training.ForType type;
}