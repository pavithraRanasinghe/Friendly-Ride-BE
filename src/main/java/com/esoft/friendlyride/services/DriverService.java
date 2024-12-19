package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.DriverRequest;
import com.esoft.friendlyride.dto.request.VehicleDetailRequest;
import com.esoft.friendlyride.exceptions.EntityNotFoundException;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Passenger;
import com.esoft.friendlyride.models.Vehicle;
import com.esoft.friendlyride.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public Driver saveDriver(DriverRequest driverRequest) {

        Driver driver = Driver.builder()
                .firstName(driverRequest.getFirstName())
                .lastName(driverRequest.getLastName())
                .email(driverRequest.getEmail())
                .nic(driverRequest.getNic())
                .contact(driverRequest.getNic())
                .build();

        return driverRepository.save(driver);
    }

    public Driver findById(final Long id){
        return driverRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(id));
    }

//    public Vehicle updateVehicleDetails(VehicleDetailRequest vehicleDetailRequest){
//        final Driver driver = findById(vehicleDetailRequest.getDriverId());
//
//    }

    public List<Driver> findNearbyDrivers(double longitude, double latitude, double distance) {
        return driverRepository.findDriversNearby(longitude, latitude, distance);
    }


    public Driver findByEmail(String email){
        return driverRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException(email));
    }

    public void updateDriver(Driver driver){
        driverRepository.save(driver);
    }
}
