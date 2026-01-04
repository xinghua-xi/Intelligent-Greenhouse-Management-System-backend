package com.smartgreenhouse.device.repository;

import com.smartgreenhouse.device.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NodeRepository extends JpaRepository<Node, String> {
    List<Node> findByGreenhouseId(String greenhouseId);
}
