package com.fiitimprove.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fiitimprove.backend.models.TrainingUser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceDTO {
    @NotNull
    Long trainingId;
    @NotNull
    LocalDateTime time;

    public AttendanceDTO(Long trainingId, LocalDateTime time) {
        this.trainingId = trainingId;
        this.time = time;
    }

    public static List<AttendanceDTO> createFromList(List<TrainingUser> tus) {
        List<AttendanceDTO> res = new ArrayList<>(tus.size());
        for (TrainingUser t : tus) {
            res.add(new AttendanceDTO(t.getTraining().getId(), t.getTraining().getTime()));
        }
        return res;
    }
}
