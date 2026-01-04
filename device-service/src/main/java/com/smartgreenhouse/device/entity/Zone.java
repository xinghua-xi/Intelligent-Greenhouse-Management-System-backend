package com.smartgreenhouse.device.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "zone")
public class Zone {
    @Id
    private String id; // e.g., "zone_001"

    private String name; // "A区 茄果类"

    @Column(name = "greenhouse_id")
    private String greenhouseId; // 所属大棚

    private String cropType; // "樱桃番茄"

    // 健康状态: HEALTHY, WARNING, CRITICAL
    private String status;
}