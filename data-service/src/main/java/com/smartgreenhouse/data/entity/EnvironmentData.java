package com.smartgreenhouse.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "environment_data")
public class EnvironmentData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "greenhouse_id")
    private String greenhouseId;

    private BigDecimal temp;
    private BigDecimal humidity;
    private Integer light;
    private Integer co2;
    private BigDecimal voltage;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
