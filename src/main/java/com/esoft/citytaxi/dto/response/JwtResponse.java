package com.esoft.citytaxi.dto.response;

import com.esoft.citytaxi.models.AppUser;
import lombok.Data;

import java.io.Serializable;

@Data
public class JwtResponse implements Serializable {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String token;

    public JwtResponse(AppUser appUser, String token) {
        this.id = appUser.getId();
        this.firstName = appUser.getFirstName();
        this.lastName = appUser.getLastName();
        this.email = appUser.getUsername();
        this.token = token;
    }
}
