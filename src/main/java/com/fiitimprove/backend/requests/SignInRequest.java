package com.fiitimprove.backend.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Sign In")
public class SignInRequest {

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Email
    private String email;
}

