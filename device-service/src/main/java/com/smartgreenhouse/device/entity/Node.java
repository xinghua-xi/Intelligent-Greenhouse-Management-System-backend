package com.smartgreenhouse.device.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "node")
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;           // 节点名称
    
    @Column(name = "greenhouse_id")
    private String greenhouseId;   // 所属大棚
    
    @Column(name = "node_type")
    private String nodeType;       // 类型: SENSOR, GATEWAY, RELAY
    
    @Column(name = "signal_strength")
    private Integer signalStrength; // 信号强度 0-100
    
    private Integer battery;       // 电量 0-100
    
    private String status;         // ONLINE, OFFLINE, WARNING
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.lastHeartbeat = LocalDateTime.now();
    }
}
