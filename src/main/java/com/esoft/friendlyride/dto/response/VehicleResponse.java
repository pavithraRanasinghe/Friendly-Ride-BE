package com.esoft.friendlyride.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class VehicleResponse implements Serializable {
    private Long id;
    private String name;
    private String model;
    private String plateNumber;
    private String colour;
}
