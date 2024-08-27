package com.esoft.friendlyride.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class DriverResponse implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String nic;
    private String contact;
    private VehicleResponse vehicle;
}
