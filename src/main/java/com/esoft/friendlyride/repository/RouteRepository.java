package com.esoft.friendlyride.repository;

import com.esoft.friendlyride.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query(value = "SELECT * FROM route WHERE " +
            "ST_DWithin(start_location, ST_SetSRID(ST_MakePoint(:startLongitude, :startLatitude), 4326), :distance) " +
            "AND ST_DWithin(end_location, ST_SetSRID(ST_MakePoint(:endLongitude, :endLatitude), 4326), :distance) " +
            "AND start_time BETWEEN CAST(:startTime AS TIME) - INTERVAL '2 HOURS' " +
            "AND CAST(:startTime AS TIME) + INTERVAL '2 HOURS' " +
            "AND start_date::date = CURRENT_DATE",
            nativeQuery = true)
    List<Route> findDriversNearby(@Param("startLongitude") double startLongitude,
                                  @Param("startLatitude") double startLatitude,
                                  @Param("distance") double distance,
                                  @Param("endLongitude") double endLongitude,
                                  @Param("endLatitude") double endLatitude,
                                  @Param("startTime") LocalTime startTime);

    @Query(value = "SELECT * FROM route WHERE " +
            "ST_DWithin(start_location, ST_SetSRID(ST_MakePoint(:startLongitude, :startLatitude), 4326), :distance) " +
            "AND start_time BETWEEN TIME(:startTime) - INTERVAL '2 HOURS' AND TIME(:startTime) + INTERVAL '2 HOURS'" +
            "AND start_date::date = CURRENT_DATE",
            nativeQuery = true)
    List<Route> findStartSearch(@Param("startLongitude") double startLongitude,
                                @Param("startLatitude") double startLatitude,
                                @Param("distance") double distance,
                                @Param("startTime") LocalTime startTime);


    @Query(value = "SELECT * FROM route WHERE " +
            "ST_DWithin(start_location, ST_SetSRID(ST_MakePoint(:startLongitude, :startLatitude), 4326), :distance) " +
            "AND ST_DWithin(end_location, ST_SetSRID(ST_MakePoint(:endLongitude, :endLatitude), 4326), :distance) " +
            "AND start_time BETWEEN TIME(:startTime) - INTERVAL '1 HOURS' AND TIME(:startTime) + INTERVAL '1 HOURS'" +
            "AND start_date::date = CURRENT_DATE",
            nativeQuery = true)
    List<Route> findEndSearch(@Param("startLongitude") double startLongitude,
                              @Param("startLatitude") double startLatitude,
                              @Param("distance") double distance,
                              @Param("endLongitude") double endLongitude,
                              @Param("endLatitude") double endLatitude,
                              @Param("startTime") LocalTime startTime);
}
