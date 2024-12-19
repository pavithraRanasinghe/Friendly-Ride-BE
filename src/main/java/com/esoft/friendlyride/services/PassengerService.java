package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.PassengerRequest;
import com.esoft.friendlyride.exceptions.EntityNotFoundException;
import com.esoft.friendlyride.models.Passenger;
import com.esoft.friendlyride.repository.PassengerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Passenger savePassenger(PassengerRequest passengerRequest) {

        Passenger passenger = Passenger.builder()
                .firstName(passengerRequest.getFirstName())
                .lastName(passengerRequest.getLastName())
                .email(passengerRequest.getEmail())
                .nic(passengerRequest.getNic())
                .contact(passengerRequest.getContact())
                .build();

        return passengerRepository.save(passenger);
    }

    public Passenger findByEmail(String email){
        return passengerRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException(email));
    }
}
