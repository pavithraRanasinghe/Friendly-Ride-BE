package com.esoft.friendlyride.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity(name = "vehicle")
public class Vehicle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String model;
    @Column(name = "plate_number")
    private String plateNumber;
    @Column(name = "colour")
    private String colour;
    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "vehicle", orphanRemoval = true)
    private Driver driver;
}
