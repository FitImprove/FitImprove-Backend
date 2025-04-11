package com.fiitimprove.backend.dto;

import com.fiitimprove.backend.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoachSearchDTO {
    private User.Gender gender;
    private String name;
    private String field;
}
