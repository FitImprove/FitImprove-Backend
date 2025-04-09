package com.fiitimprove.backend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForType forType = ForType.EVERYONE;

    @Column(name = "time_date_and_time", nullable = false)
    private LocalDateTime timeDateAndTime;

    @Column(name = "free_slots")
    @Min(value = 0, message = "FreeSlots can not be negative")
    private int freeSlots;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "is_canceled")
    private boolean isCanceled = false;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TrainingUser> trainingUsers = new ArrayList<>();

    public enum ForType {
        EVERYONE, LIMITED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
