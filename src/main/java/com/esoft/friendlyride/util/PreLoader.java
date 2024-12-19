package com.esoft.friendlyride.util;

import com.esoft.friendlyride.dto.request.AppUserRequest;
import com.esoft.friendlyride.enums.UserType;
import com.esoft.friendlyride.models.AppUser;
import com.esoft.friendlyride.repository.AppUserRepository;
import com.esoft.friendlyride.services.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Configuration
public class PreLoader {

    @Bean
    CommandLineRunner initDatabase(AppUserService appUserService,
                                   PasswordEncoder passwordEncoder){
        return args -> {
            if(Objects.isNull(appUserService.findByUsername("admin@a.com"))){
                appUserService.signUp(AppUserRequest.builder()
                                .firstName("Admin")
                                .lastName("Esoft")
                                .contact("1234567890")
                                .username("admin@a.com")
                                .password("admin")
                                .userType(UserType.ADMIN).build());
            }
            if(Objects.isNull(appUserService.findByUsername("driver@d.com"))){
                appUserService.signUp(AppUserRequest.builder()
                        .firstName("Driver")
                        .lastName("Esoft")
                        .contact("1234567890")
                        .username("driver@d.com")
                        .password("driver")
                        .userType(UserType.DRIVER).build());
            }
            if(Objects.isNull(appUserService.findByUsername("passenger@p.com"))){
                appUserService.signUp(AppUserRequest.builder()
                        .firstName("Passenger")
                        .lastName("Esoft")
                        .contact("1234567890")
                        .username("passenger@p.com")
                        .password("passenger")
                        .userType(UserType.PASSENGER).build());
            }
        };
    }
}
