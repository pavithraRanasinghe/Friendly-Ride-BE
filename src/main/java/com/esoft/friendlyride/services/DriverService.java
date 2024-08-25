package com.esoft.friendlyride.services;

import com.esoft.friendlyride.dto.request.DriverRequest;
import com.esoft.friendlyride.models.Driver;
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
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public Driver saveDriver(DriverRequest driverRequest) {

        Coordinate coordinate = new Coordinate(driverRequest.getLongitude(), driverRequest.getLatitude());
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326);

        Driver driver = Driver.builder()
                .name(driverRequest.getName())
                .location(point)
                .build();

        return driverRepository.save(driver);
    }

    public List<Driver> findNearbyDrivers(double longitude, double latitude, double distance) {
        return driverRepository.findDriversNearby(longitude, latitude, distance);
    }
}
