package com.fiitimprove.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Coach.class, name = "COACH"),
        @JsonSubTypes.Type(value = RegularUser.class, name = "USER")
})
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Base class for users (RegularUser or Coach)", subTypes = {Coach.class, RegularUser.class})
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name can not be null/empty")
    @Size(min = 1, max = 128, message = "Name must be between 1 and 128 characters")
    @Pattern(regexp = "^[\\p{L}]+$", message = "Name can only contain letters from any language")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Surname can not be null/empty")
    @Size(min = 1, max = 128, message = "Surname must be between 1 and 128 characters")
    @Pattern(regexp = "^[\\p{L}]+$", message = "Surname can only contain letters from any language")
    private String surname;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Username can not be null/empty")
    @Size(min = 3, max = 128, message = "Username must be between 3 and 128 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only consist of english letters, digits and _")
    private String username;

    @Column(nullable = false, unique = true)
    @NotNull(message = "email can not be null/empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "password can not be null")
   // @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,32}$", message = "Password has to have lower and upper case en letter and some digit.")
    // @Pattern(regexp = "^(?!.*[<>\"'&;])$", message = "Password cannot contain malicious characters such as <, >, \", ', &, or ;.")
    private String password;

    @Enumerated(EnumType.STRING)
    // @Pattern(regexp = "^(|MALE|FEMALE)$", message = "Gender has to belong to {MALE | FEMALE}")
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "dateOfBirth can not be null")
    private LocalDate dateOfBirth;

    private String links;

    @Column(name = "last_time_online")
    private LocalDate lastTimeOnline;

    @Column(name = "joined_at", updatable = false)
    private LocalDate joinedAt;
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDate.now();
    }
    public void hashPassword(PasswordEncoder passwordEncoder) {
        if (password == null || password.length() < 6 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must be at least 6 characters long, contain an uppercase letter, a lowercase letter, and a digit.");
        }
        this.password = passwordEncoder.encode(this.password);
    }

    @Column(name = "role", insertable = false, updatable = false)
    @Pattern(regexp = "^(USER|COACH)$", message = "Field validator allows only role states: {USER | COACH}")
    @JsonIgnore
    private String role;

    @Column(name = "self_information")
    @Size(min = 8, max = 1024, message = "Desription must be between 8 and 1024 characters")
    @Pattern(regexp = "^[\\p{L}\\d\\s.,#!?@(){}\\[\\]<>%&'*+_-]+$", message = "Input must contain only letters, digits, whitespace, and common punctuation marks.") // ?
    private String selfInformation;

    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private Settings settings;

    public enum Gender {
        MALE, FEMALE
    }
}