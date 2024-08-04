package com.esoft.citytaxi.security;

import com.esoft.citytaxi.models.AppUser;
import com.esoft.citytaxi.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * The type Jwt user details service.
 */
@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final AppUserService appUserService;

    /**
     * Instantiates a new Jwt user details service.
     *
     * @param appUserService the user service
     */
    public JwtUserDetailsService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AppUser registeredUser = appUserService.findByUsername(username);
            return new User(registeredUser.getUsername(), registeredUser.getPassword(), Collections.emptyList());
        } catch (Exception ex) {
            log.error("Request format error", ex);
            throw new UsernameNotFoundException("Request format Error");
        }
    }

}
