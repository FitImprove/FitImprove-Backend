package com.fiitimprove.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fiitimprove.backend.models.TrainingUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingUserDTO {
    private Long id;
    private Long userId;
    private Long trainingId;
    private TrainingUser.Status status;
    private LocalDateTime trainingTime;
    private LocalDateTime invitedAt;
    private LocalDateTime bookedAt;
    private LocalDateTime canceledAt;

    public TrainingUserDTO(Long id, Long userId, Long trainingId, TrainingUser.Status status, 
                           LocalDateTime trainingTime, LocalDateTime invitedAt, 
                           LocalDateTime bookedAt, LocalDateTime canceledAt) {
        this.id = id;
        this.userId = userId;
        this.trainingId = trainingId;
        this.status = status;
        this.trainingTime = trainingTime;
        this.invitedAt = invitedAt;
        this.bookedAt = bookedAt;
        this.canceledAt = canceledAt;
    }

    public static TrainingUserDTO create(TrainingUser tr) {
        return new TrainingUserDTO(
            tr.getId(), 
            tr.getUser().getId(), 
            tr.getTraining().getId(), 
            tr.getStatus(), 
            tr.getTraining().getTime(), 
            tr.getInvitedAt(), 
            tr.getBookedAt(), 
            tr.getCanceledAt()
        );
    }

    public static List<TrainingUserDTO> createList(List<TrainingUser> trs) {
        List<TrainingUserDTO> arr = new ArrayList<>();
        for (TrainingUser tr : trs) {
            arr.add(TrainingUserDTO.create(tr));
        }
        return arr;
    }
}
