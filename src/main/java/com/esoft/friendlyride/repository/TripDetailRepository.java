package com.esoft.friendlyride.repository;

import com.esoft.friendlyride.models.Passenger;
import com.esoft.friendlyride.models.TripDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripDetailRepository extends JpaRepository<TripDetail, Long> {

    List<TripDetail> findByPassenger(Passenger passenger);
}
