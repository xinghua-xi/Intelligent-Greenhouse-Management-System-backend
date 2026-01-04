package com.smartgreenhouse.device.repository;

import com.smartgreenhouse.device.entity.Greenhouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenhouseRepository extends JpaRepository<Greenhouse, String> {
}