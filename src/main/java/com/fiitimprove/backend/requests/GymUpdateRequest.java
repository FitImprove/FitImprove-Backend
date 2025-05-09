package com.fiitimprove.backend.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GymUpdateRequest {

    private double latitude;

    private double longitude;

    private String address;
}
