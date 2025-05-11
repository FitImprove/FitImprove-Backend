package com.fiitimprove.backend.dto;

import com.fiitimprove.backend.models.TrainingUser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubUserForTrainingDTO {
    @NotNull
    private Long userId;
    @NotNull
    private String userName;
    @NotNull
    private Long trainingId;
    @NotNull
    private TrainingUser.Status status;
    @NotNull
    private String iconPath;

    public PubUserForTrainingDTO (Long userId, String userName, Long trainingId, TrainingUser.Status status, String iconPath) {
        this.userId = userId;
        this.userName = userName;
        this.trainingId = trainingId;
        this.status = status;
        this.iconPath = iconPath;
    }
}
