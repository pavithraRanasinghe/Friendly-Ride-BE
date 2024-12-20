package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.RouteRequest;
import com.esoft.friendlyride.dto.request.SearchDriverRequest;
import com.esoft.friendlyride.dto.request.TripRequest;
import com.esoft.friendlyride.dto.response.*;
import com.esoft.friendlyride.enums.TripStatus;
import com.esoft.friendlyride.exceptions.EntityNotFoundException;
import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Passenger;
import com.esoft.friendlyride.models.Route;
import com.esoft.friendlyride.models.TripDetail;
import com.esoft.friendlyride.repository.RouteRepository;
import com.esoft.friendlyride.repository.TripDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final DriverService driverService;
    private final PassengerService passengerService;
    private final TripDetailRepository tripDetailRepository;

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
                    .price(calculatePrice(route.getStartLocation(), route.getEndLocation(), route.getMaxPassengers(), route.getStartTime()))
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
                        .price(calculatePrice(startSearch.getStartLocation(), endSearch.getEndLocation(), startSearch.getMaxPassengers(), startSearch.getStartTime()))
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
                .id(route.getId())
                .date(route.getDate())
                .startTime(route.getStartTime())
                .expectedEndTime(route.getExpectedEndTime())
                .maxPassengers(route.getMaxPassengers())
                .startLatitude(route.getStartLocation().getX())
                .startLongitude(route.getStartLocation().getY())
                .endLatitude(route.getEndLocation().getX())
                .endLongitude(route.getEndLocation().getY())
                .driverResponse(DriverResponse.builder()
                        .id(route.getDriver().getId())
                        .firstName(route.getDriver().getFirstName())
                        .lastName(route.getDriver().getLastName())
                        .email(route.getDriver().getEmail())
                        .contact(route.getDriver().getContact())
                        .nic(route.getDriver().getNic())
                        .vehicle(VehicleResponse.builder()
                                .id(route.getDriver().getVehicle().getId())
                                .name(route.getDriver().getVehicle().getName())
                                .model(route.getDriver().getVehicle().getModel())
                                .colour(route.getDriver().getVehicle().getColour())
                                .plateNumber(route.getDriver().getVehicle().getPlateNumber())
                                .build())
                        .build())
                .build();
    }

    private Point mapToPoint(final double longitude,final double latitude){
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);
        return point;
    }

    private BigDecimal calculatePrice(Point start, Point end, int maxPassengers, LocalTime startTime) {
        // Distance in kilometers
        double distance = calculateDistanceInKilometers(start, end);

        // Base fare
        BigDecimal baseFare = BigDecimal.valueOf(5.00);
        // Per kilometer fare
        BigDecimal perKilometerFare = BigDecimal.valueOf(2.00);
        // Additional charge per passenger beyond the first one
        int additionalPassengers = maxPassengers > 1 ? maxPassengers - 1 : 0;
        BigDecimal additionalPassengerCharge = BigDecimal.valueOf(1.00).multiply(BigDecimal.valueOf(additionalPassengers));

        // Time-based surcharge for peak hours
        BigDecimal timeSurcharge = BigDecimal.ONE;
        if (startTime.isAfter(LocalTime.of(17, 0)) && startTime.isBefore(LocalTime.of(20, 0))) {
            timeSurcharge = BigDecimal.valueOf(1.20);
        }

        // Calculate the total price
        BigDecimal distanceCharge = perKilometerFare.multiply(BigDecimal.valueOf(distance));
        BigDecimal totalPrice = baseFare.add(distanceCharge).add(additionalPassengerCharge).multiply(timeSurcharge);

        return totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP); // Round to 2 decimal places
    }

    private double calculateDistanceInKilometers(Point start, Point end) {
        return DistanceOp.distance(start.getBoundary(), end.getBoundary()) / 1000.0; // Convert meters to kilometers
    }

    public void bookTrip(final TripRequest tripRequest){
        Passenger passenger = passengerService.findById(tripRequest.getPassengerId());
        tripRequest.getRouteIds().forEach(routeId ->{
            Route route = findById(routeId);
            route.setReservePassengers(route.getReservePassengers() + 1);
            if(route.getMaxPassengers() == route.getReservePassengers()){
                route.setStatus(TripStatus.OCCUPIED);
            }

            TripDetail tripDetail =  TripDetail.builder()
                    .route(route)
                    .passenger(passenger)
                    .status(TripStatus.PENDING)
                    .build();
            tripDetailRepository.save(tripDetail);
        });
    }

    public List<TripDetailResponse> findByPassenger(final Long passengerId) {
        Passenger passenger = passengerService.findById(passengerId);
        List<TripDetail> tripDetails = tripDetailRepository.findByPassenger(passenger);

        return tripDetails.stream().map(tripDetail -> TripDetailResponse.builder()
                        .passenger(tripDetail.getPassenger())
                        .driverResponse(DriverResponse.builder()
                                .id(tripDetail.getRoute().getDriver().getId())
                                .firstName(tripDetail.getRoute().getDriver().getFirstName())
                                .lastName(tripDetail.getRoute().getDriver().getLastName())
                                .email(tripDetail.getRoute().getDriver().getEmail())
                                .nic(tripDetail.getRoute().getDriver().getNic())
                                .contact(tripDetail.getRoute().getDriver().getContact())
                                .vehicle(VehicleResponse.builder()
                                        .id(tripDetail.getRoute().getDriver().getVehicle().getId())
                                        .name(tripDetail.getRoute().getDriver().getVehicle().getName())
                                        .model(tripDetail.getRoute().getDriver().getVehicle().getModel())
                                        .plateNumber(tripDetail.getRoute().getDriver().getVehicle().getPlateNumber())
                                        .colour(tripDetail.getRoute().getDriver().getVehicle().getColour())
                                        .build())
                                .build())
                        .routeId(tripDetail.getRoute().getId())
                        .date(tripDetail.getRoute().getDate())
                        .startTime(tripDetail.getRoute().getStartTime())
                        .expectedEndTime(tripDetail.getRoute().getExpectedEndTime())
                        .startLatitude(tripDetail.getRoute().getStartLocation().getY())
                        .startLongitude(tripDetail.getRoute().getStartLocation().getX())
                        .endLatitude(tripDetail.getRoute().getEndLocation().getY())
                        .endLongitude(tripDetail.getRoute().getEndLocation().getX())
                        .build())
                .collect(Collectors.toList());
    }

}
