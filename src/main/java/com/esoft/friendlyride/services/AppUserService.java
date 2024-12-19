package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.AppUserRequest;
import com.esoft.friendlyride.dto.request.AuthRequest;
import com.esoft.friendlyride.dto.request.DriverRequest;
import com.esoft.friendlyride.dto.request.PassengerRequest;
import com.esoft.friendlyride.enums.UserType;
import com.esoft.friendlyride.exceptions.EntityExistsException;
import com.esoft.friendlyride.exceptions.EntityNotFoundException;
import com.esoft.friendlyride.exceptions.UnAuthorizedException;
import com.esoft.friendlyride.models.AppUser;
import com.esoft.friendlyride.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverService driverService;
    private final PassengerService passengerService;

    @Autowired
    public AppUserService(final AppUserRepository appUserRepository,
                          final PasswordEncoder passwordEncoder,
                          final DriverService driverService,
                          final PassengerService passengerService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverService = driverService;
        this.passengerService = passengerService;
    }

    /**
     * Method for save application user
     *
     * @param userRequest {@link AppUserRequest} user detail request
     * @return {@link AppUser} Application user model
     */
    public AppUser signUp(final AppUserRequest userRequest){
        log.info("Save new user : {}", userRequest);

        AppUser registeredUser = this.findByUsername(userRequest.getUsername());
        if(Objects.nonNull(registeredUser))
            throw new EntityExistsException("User already registered ", userRequest.getUsername());
        final AppUser appUser = AppUser.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .userType(userRequest.getUserType())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();

        AppUser user = appUserRepository.save(appUser);

        if(user.getUserType().equals(UserType.DRIVER)){
            driverService.saveDriver(DriverRequest.builder()
                    .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .nic("123")
                            .email(user.getUsername())
                            .contact(userRequest.getContact())
                    .build());
        } else if(user.getUserType().equals(UserType.PASSENGER)){
            passengerService.savePassenger(PassengerRequest.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .nic("123")
                    .email(user.getUsername())
                    .contact(userRequest.getContact())
                    .build());
        }

        return user;
    }

    public AppUser findByUsername(final String username){
        return appUserRepository.findByUsername(username)
                .orElse(null);
    }

    public AppUser authenticate(AuthRequest authRequest) {
        AppUser existUser = this.appUserRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(()-> new EntityNotFoundException(authRequest.getUsername()));
        if (passwordEncoder.matches(authRequest.getPassword(), existUser.getPassword())){
            return existUser;
        }else {
            throw new UnAuthorizedException("Password doesn't match for user");
        }
    }

    public Long findRelatedUserId(final String username, final UserType userType){
        if(userType.equals(UserType.DRIVER)){
            return driverService.findByEmail(username).getId();
        }else if(userType.equals(UserType.PASSENGER)){
            return passengerService.findByEmail(username).getId();
        }
        return null;
    }
}
