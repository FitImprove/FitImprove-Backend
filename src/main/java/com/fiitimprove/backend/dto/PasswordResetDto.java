package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO that stores information sent by user for password recovery
 */
@Getter
@Setter
public class PasswordResetDto {
    @NotNull
    private String token;

    @NotNull
    private String password;
}