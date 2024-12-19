package com.esoft.friendlyride.controller;

import com.esoft.friendlyride.dto.request.VehicleRequest;
import com.esoft.friendlyride.services.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/vehicle")
@Slf4j
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public void save(@RequestBody VehicleRequest vehicleRequest){
        vehicleService.save(vehicleRequest);
    }

}
