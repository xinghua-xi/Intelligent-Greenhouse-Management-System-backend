package com.smartgreenhouse.device.repository;

import com.smartgreenhouse.device.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, String> {
    List<Zone> findByGreenhouseId(String greenhouseId);
}
