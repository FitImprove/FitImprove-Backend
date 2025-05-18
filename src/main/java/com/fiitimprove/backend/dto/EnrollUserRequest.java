package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for user's enrolment in a training
 */
@Getter
@Setter
public class EnrollUserRequest {
    @NotNull
    private Long trainingId;
}