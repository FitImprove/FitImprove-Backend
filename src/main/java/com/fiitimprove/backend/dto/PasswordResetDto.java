package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDto {
    @NotNull
    private String token;

    @NotNull
    private String password;
}