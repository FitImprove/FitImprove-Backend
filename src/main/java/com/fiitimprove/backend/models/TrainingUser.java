package com.fiitimprove.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fiitimprove.backend.repositories.TrainingUserRepository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "training_users")
@Getter
@Setter
public class TrainingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    @JsonBackReference
    private Training training;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private RegularUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.INVITED;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public enum Status {
        INVITED, AGREED, DENIED, CANCELED
    }

    @PrePersist
    protected void onCreate() {
        if (status == Status.INVITED) {
            invitedAt = LocalDateTime.now();
        }
    }
}