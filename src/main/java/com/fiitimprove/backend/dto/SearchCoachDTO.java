package com.fiitimprove.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO that stores information required to make a coach search
 */
@Getter
@Setter
public class SearchCoachDTO {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String fields;
    private String iconPath;
    private String gymAddress;

    public SearchCoachDTO (Long userId, String userName, String iconPath, String gymAddress) {
        this.id = userId;
        this.name = userName;
        this.iconPath = iconPath;
        this.gymAddress = gymAddress;
    }
}
