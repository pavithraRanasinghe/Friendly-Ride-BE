package com.esoft.friendlyride.repository;

import com.esoft.friendlyride.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
