package com.smartgreenhouse.device.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alert")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String greenhouseId;

    // 告警代码: E01(温度过高), E02(红蜘蛛)
    private String code;

    private String message; // "A区检测到红蜘蛛"

    // 级别: CRITICAL, WARNING, INFO
    private String severity;

    // 状态: PENDING(待处理), RESOLVED(已解决)
    private String status;

    private LocalDateTime createdAt;
}