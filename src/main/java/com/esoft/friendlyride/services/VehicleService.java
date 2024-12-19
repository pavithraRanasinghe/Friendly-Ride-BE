package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.VehicleRequest;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Vehicle;
import com.esoft.friendlyride.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverService driverService;

    public VehicleService(final VehicleRepository vehicleRepository,
                          final DriverService driverService) {
        this.vehicleRepository = vehicleRepository;
        this.driverService = driverService;
    }

    public void save(final VehicleRequest vehicleRequest){
        Driver driver = driverService.findById(vehicleRequest.getDriverId());

        Vehicle vehicle = Vehicle.builder()
                .model(vehicleRequest.getModel())
                .colour(vehicleRequest.getColour())
                .name(vehicleRequest.getName())
                .plateNumber(vehicleRequest.getPlateNumber())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        driver.setVehicle(savedVehicle);

        driverService.updateDriver(driver);
    }
}
