package com.fiitimprove.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GymUpdateRequest {

    private double latitude;

    private double longitude;

    private String address;
}
