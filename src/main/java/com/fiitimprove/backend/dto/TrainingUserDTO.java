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
    private Long trainingid;
    private TrainingUser.Status status;
    private LocalDateTime trainingTime;
    private LocalDateTime invitedAt;
    private LocalDateTime bookedAt;
    private LocalDateTime canceledAt;

    public TrainingUserDTO(Long id, Long userId, Long trainingid, TrainingUser.Status status, 
                           LocalDateTime trainingTime, LocalDateTime invitedAt, 
                           LocalDateTime bookedAt, LocalDateTime canceledAt) {
        this.id = id;
        this.userId = userId;
        this.trainingid = trainingid;
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
            tr.getTraining().getTimeDateAndTime(), 
            tr.getInvitedAt(), 
            tr.getBookedAt(), 
            tr.getCanceledAt()
        );
    }

    public static List<TrainingUserDTO> convertList(List<TrainingUser> trs) {
        List<TrainingUserDTO> arr = new ArrayList<>();
        for (TrainingUser tr : trs) {
            arr.add(TrainingUserDTO.create(tr));
        }
        return arr;
    }
}
