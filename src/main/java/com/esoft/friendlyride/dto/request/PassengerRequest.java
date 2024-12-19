package com.esoft.friendlyride.dto.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PassengerRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String nic;
    private String contact;
}
