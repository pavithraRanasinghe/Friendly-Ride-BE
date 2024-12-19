package com.esoft.friendlyride.dto.response;

import com.esoft.friendlyride.enums.UserType;
import com.esoft.friendlyride.models.AppUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class JwtResponse implements Serializable {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String token;
    private final UserType userType;

    public JwtResponse(AppUser appUser, String token, final Long userId) {
        this.id = Objects.isNull(userId) ? appUser.getId() : userId;
        this.firstName = appUser.getFirstName();
        this.lastName = appUser.getLastName();
        this.email = appUser.getUsername();
        this.token = token;
        this.userType = appUser.getUserType();
    }
}
