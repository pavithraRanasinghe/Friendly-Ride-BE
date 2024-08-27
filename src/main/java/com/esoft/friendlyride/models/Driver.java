package com.esoft.friendlyride.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity(name = "driver")
public class Driver implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email", unique = true)
    private String email;
    private String nic;
    private String contact;
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "driver", orphanRemoval = true)
    private List<Route> routes;
    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
