package com.fiitimprove.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("COACH")
@Getter
@Setter
public class Coach extends User {

    @ElementCollection
    private List<String> fields;

    @ElementCollection
    private List<String> skills;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    @Column(name = "works_in_field_since")
    private LocalDate worksInFieldSince;
}