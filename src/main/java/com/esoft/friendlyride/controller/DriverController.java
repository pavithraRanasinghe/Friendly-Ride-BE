package com.esoft.friendlyride.controller;

import com.esoft.friendlyride.dto.request.DriverRequest;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.services.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/driver")
@Slf4j
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<Driver> addDriver(@RequestBody DriverRequest driverRequest) {
        Driver driver = driverService.saveDriver(driverRequest);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/nearby")
    public List<Driver> getNearbyDrivers(@RequestParam double longitude, @RequestParam double latitude, @RequestParam double distance) {
        return driverService.findNearbyDrivers(longitude, latitude, distance);
    }
}
