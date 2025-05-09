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

    @Column(nullable = false)
    private String type;

    @Column(name = "time_date_and_time", nullable = false)
    private LocalDateTime time;

    @Column(name = "free_slots")
    @Min(value = 0, message = "FreeSlots can not be negative")
    private int freeSlots;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "duration", nullable = false)
    private int durationMinutes;

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

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }
}
