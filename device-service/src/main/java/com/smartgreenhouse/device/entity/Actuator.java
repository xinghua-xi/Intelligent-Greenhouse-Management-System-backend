package com.smartgreenhouse.device.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "actuator")
public class Actuator {
    @Id
    private String id;

    private String name; // "1号风机", "顶部补光灯"

    @Column(name = "zone_id")
    private String zoneId; // 所属分区

    // 设备类型: FAN, LIGHT, PUMP, HEATER
    private String type;

    // 当前值: "ON", "OFF", "80%" (PWM控制用)
    private String currentValue;

    // 是否自动托管
    private Boolean autoMode;
}
