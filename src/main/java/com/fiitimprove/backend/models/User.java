package com.fiitimprove.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    private String links;

    @Column(name = "last_time_online")
    private LocalDate lastTimeOnline;

    @Column(name = "joined_at", updatable = false)
    private LocalDate joinedAt;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    @Column(name = "self_information")
    private String selfInformation;

    @OneToOne(mappedBy = "user")
    private Settings settings;
    public enum Gender {
        MALE, FEMALE
    }
}