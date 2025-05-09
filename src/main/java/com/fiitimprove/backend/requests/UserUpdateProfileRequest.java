package com.fiitimprove.backend.requests;

import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.models.Settings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class UserUpdateProfileRequest {
    private String name;
    private String surname;
    private String username;
    private User.Gender gender;
    private String dateOfBirth;
    private String links;
    private String selfInformation;
    private List<String> fields;
    private List<String> skills;
    private String selfIntroduction;
    private LocalDate worksInFieldSince;
}