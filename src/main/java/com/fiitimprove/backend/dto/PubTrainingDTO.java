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

    public PubTrainingDTO(Long id, ForType forType, 
                    int freeSlots, LocalDateTime createdAt, 
                    String title, String description, boolean isCanceled,
                    LocalDateTime time, int durationMinutes) {
        this.id = id;
        this.forType = forType;
        this.freeSlots = freeSlots;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.isCanceled = isCanceled;
        this.time = time;
        this.durationMinutes = durationMinutes;
    }

    public static PubTrainingDTO create(Training training) {
        return new PubTrainingDTO(
            training.getId(),
            training.getForType(),
            training.getFreeSlots(),
            training.getCreatedAt(),
            training.getTitle(),
            training.getDescription(),
            training.isCanceled(),
            training.getTime(),
            training.getDurationMinutes()
        );
    } 

    public static List<PubTrainingDTO> createForList(List<Training> trs) {
        List<PubTrainingDTO> res = new ArrayList<>(trs.size());
        for (Training tr : trs) {
            res.add(PubTrainingDTO.create(tr));
        }
        return res;
    }
}
