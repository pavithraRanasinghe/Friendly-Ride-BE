package com.esoft.friendlyride.controller;

import com.esoft.friendlyride.dto.request.RouteRequest;
import com.esoft.friendlyride.dto.request.SearchDriverRequest;
import com.esoft.friendlyride.dto.response.MultipleRouteSearchResponse;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Route;
import com.esoft.friendlyride.services.RouteService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("v1/route")
@Slf4j
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public Route save(@RequestBody RouteRequest routeRequest){
        return routeService.saveRoute(routeRequest);
    }

    @GetMapping("/search")
    public List<MultipleRouteSearchResponse> getNearbyDrivers(
            @RequestParam double startLongitude,
            @RequestParam double startLatitude,
            @RequestParam double endLongitude,
            @RequestParam double endLatitude,
            @JsonDeserialize(using = LocalTimeDeserializer.class)
            @JsonSerialize(using = LocalTimeSerializer.class)
            @JsonFormat(shape = JsonFormat.Shape.STRING)
            @RequestParam LocalTime startTime) {
        return routeService.findTrip(
                SearchDriverRequest.builder()
                        .startLongitude(startLongitude)
                        .startLatitude(startLatitude)
                        .endLongitude(endLongitude)
                        .endLatitude(endLatitude)
                        .startTime(startTime)
                        .build());
    }
}
