package com.esoft.friendlyride.repository;

import com.esoft.friendlyride.models.Driver;
import com.esoft.friendlyride.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT * FROM drivers WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distance)", nativeQuery = true)
    List<Driver> findDriversNearby(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("distance") double distance);

    Optional<Driver> findByEmail(String email);
}
