package com.esoft.friendlyride.dto.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class VehicleRequest implements Serializable {
    private String name;
    private String model;
    private String plateNumber;
    private String colour;
    private Long driverId;
}

