package com.fiitimprove.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Theme cannot be null")
    private Theme theme = Theme.PURPLE;

    @Min(value = 8, message = "Font size must be at least 8")
    @Column(name = "font_size")
    private int fontSize = 12;

    @NotNull(message = "Notifications setting cannot be null")
    private Boolean notifications;


    public enum Theme {
        PURPLE, BLACK, HIGH_CONTRAST
    }
}
