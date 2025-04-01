package com.fiitimprove.backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("USER")
@Getter
@Setter
public class RegularUser extends User {

}