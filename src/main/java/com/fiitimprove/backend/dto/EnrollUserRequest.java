package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollUserRequest {
    @NotNull
    private Long trainingId;

    @NotNull
    private Long userId;
}