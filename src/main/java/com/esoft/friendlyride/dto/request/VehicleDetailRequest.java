package com.esoft.friendlyride.dto.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class VehicleDetailRequest implements Serializable {
    private Long id;
    private String name;
    private String model;
    private String plateNumber;
    private String colour;
    private Long driverId;
}
