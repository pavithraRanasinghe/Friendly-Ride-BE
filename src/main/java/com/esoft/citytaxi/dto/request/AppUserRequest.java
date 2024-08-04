package com.esoft.citytaxi.dto.request;

import com.esoft.citytaxi.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String contact;
    private UserType userType;
}
