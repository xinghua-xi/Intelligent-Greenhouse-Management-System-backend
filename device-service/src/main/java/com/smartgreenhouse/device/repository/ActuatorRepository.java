package com.smartgreenhouse.device.repository;

import com.smartgreenhouse.device.entity.Actuator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActuatorRepository extends JpaRepository<Actuator, String> {
    List<Actuator> findByZoneId(String zoneId);
}
