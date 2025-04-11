package com.fiitimprove.backend.requests;

import com.fiitimprove.backend.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class UserUpdateProfileRequest {
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private User.Gender gender;
    private LocalDate dateOfBirth;
    private String links;
    private String selfInformation;
    private List<String> fields;
    private List<String> skills;
    private String selfIntroduction;
    private LocalDate worksInFieldSince;
}
