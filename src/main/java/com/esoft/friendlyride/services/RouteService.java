package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.RouteRequest;
import com.esoft.friendlyride.dto.request.SearchDriverRequest;
import com.esoft.friendlyride.dto.response.DriverResponse;
import com.esoft.friendlyride.dto.response.MultipleRouteSearchResponse;
import com.esoft.friendlyride.dto.response.RouteSearchResponse;
import com.esoft.friendlyride.dto.response.VehicleResponse;
import com.esoft.friendlyride.exceptions.EntityNotFoundException;
import com.esoft.friendlyride.exceptions.NotFoundException;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Route;
import com.esoft.friendlyride.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final DriverService driverService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    public Route saveRoute(final RouteRequest routeRequest) {

        Driver driver = driverService.findById(routeRequest.getDriverId());

        final Point startCoordination =  mapToPoint(routeRequest.getStartLongitude(), routeRequest.getStartLatitude());
        final Point endCoordination =  mapToPoint(routeRequest.getEndLongitude(), routeRequest.getEndLatitude());

        Route route = new Route();
//        if(Objects.nonNull(routeRequest.getId())){
//            Optional<Route> existingRoute = routeRepository.findById(routeRequest.getId());
//            if(existingRoute.isPresent()){
//                route = existingRoute.get();
//            }
//        }
        route.setRouteId(UUID.randomUUID().toString());

        route.setDate(routeRequest.getStartDate());
        route.setStartTime(routeRequest.getStartTime());
        route.setExpectedEndTime(routeRequest.getEndTime());
        route.setStartLocation(startCoordination);
        route.setEndLocation(endCoordination);
        route.setMaxPassengers(routeRequest.getMaxPassengers());
        route.setDriver(driver);

        return routeRepository.save(route);
    }

    public List<MultipleRouteSearchResponse> findTrip(final SearchDriverRequest searchRequest){
        final double distance = .2;
        List<Route> singleRideList = routeRepository.findDriversNearby(
                searchRequest.getStartLongitude(),
                searchRequest.getStartLatitude(),
                distance,
                searchRequest.getEndLongitude(),
                searchRequest.getEndLatitude()
//                searchRequest.getStartTime()
        );

        if(!singleRideList.isEmpty()){
            return singleRideList.stream().map(route -> MultipleRouteSearchResponse.builder()
                    .firstRoute(convertRouteToResponse(route))
                    .secondRoute(null)
                    .startTime(route.getStartTime())
                    .expectedEndTime(route.getExpectedEndTime())
                    .build()).toList();

        }
        List<MultipleRouteSearchResponse> searchResult = new ArrayList<>();
        List<Route> startSearchList = routeRepository.findStartSearch(
                searchRequest.getStartLongitude(),
                searchRequest.getStartLatitude(), distance);

        startSearchList.forEach(startSearch -> {
            double midLongitude = startSearch.getEndLocation().getX();
            double midLatitude = startSearch.getEndLocation().getY();

            List<Route> endSearchList = routeRepository.findEndSearch(
                    midLongitude,
                    midLatitude,
                    distance,
                    searchRequest.getEndLongitude(),
                    searchRequest.getEndLatitude());

            endSearchList.forEach(endSearch->{
                MultipleRouteSearchResponse result = MultipleRouteSearchResponse.builder()
                        .firstRoute(convertRouteToResponse(startSearch))
                        .secondRoute(convertRouteToResponse(endSearch))
                        .startTime(startSearch.getStartTime())
                        .expectedEndTime(endSearch.getExpectedEndTime())
                        .build();

                searchResult.add(result);
            });
        });
        return searchResult;
    }

    public Route findById(Long id){
        return routeRepository.findById(id).orElseThrow(()->new EntityNotFoundException(id));
    }

    private RouteSearchResponse convertRouteToResponse(final Route route){
        return RouteSearchResponse.builder()
                .startTime(route.getStartTime())
                .expectedEndTime(route.getExpectedEndTime())
                .driverResponse(DriverResponse.builder()
                        .id(route.getDriver().getId())
                        .firstName(route.getDriver().getFirstName())
                        .lastName(route.getDriver().getLastName())
                        .email(route.getDriver().getEmail())
                        .contact(route.getDriver().getContact())
                        .nic(route.getDriver().getNic())
//                        .vehicle(VehicleResponse.builder()
//                                .id(route.getDriver().getVehicle().getId())
//                                .name(route.getDriver().getVehicle().getName())
//                                .model(route.getDriver().getVehicle().getModel())
//                                .colour(route.getDriver().getVehicle().getColour())
//                                .plateNumber(route.getDriver().getVehicle().getPlateNumber())
//                                .build())
                        .build())
                .build();
    }

    private Point mapToPoint(final double longitude,final double latitude){
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);
        return point;
    }
}
