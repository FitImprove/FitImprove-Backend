package com.fiitimprove.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.Training.ForType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PubTrainingDTO {
    private Long id;
    private Training.ForType forType;
    private int freeSlots;
    private LocalDateTime createdAt;
    private String title;
    private String description;
    private boolean isCanceled;
    private LocalDateTime time;
    private int durationMinutes;
    private Long coachId;
    private String coachName;
    private String gymName;

    public PubTrainingDTO(Training training) {
        this.id = training.getId();
        this.forType = training.getForType();
        this.freeSlots = training.getFreeSlots();
        this.createdAt = training.getCreatedAt();
        this.title = training.getTitle();
        this.description = training.getDescription();
        this.isCanceled = training.isCanceled();
        this.time = training.getTime();
        this.durationMinutes = training.getDurationMinutes();
        this.coachId = training.getCoach().getId();
        this.coachName = training.getCoach().getName() + " " + training.getCoach().getSurname();
        var gym = training.getCoach().getGym();
        this.gymName = (gym != null) ? gym.getAddress() : "NoAddress";
    }

    public static PubTrainingDTO create(Training training) {
        return new PubTrainingDTO(training);
    } 

    public static List<PubTrainingDTO> createForList(List<Training> trs) {
        List<PubTrainingDTO> res = new ArrayList<>(trs.size());
        for (Training tr : trs) {
            res.add(PubTrainingDTO.create(tr));
        }
        return res;
    }
}
