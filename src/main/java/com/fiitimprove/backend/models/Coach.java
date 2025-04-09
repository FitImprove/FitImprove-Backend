package com.fiitimprove.backend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("COACH")
@Getter
@Setter
@NoArgsConstructor
public class Coach extends User {

    @ElementCollection
    private List<String> fields;

    @ElementCollection
    private List<String> skills;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    @Column(name = "works_in_field_since")
    private LocalDate worksInFieldSince;

    @OneToOne(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Gym gym;
}