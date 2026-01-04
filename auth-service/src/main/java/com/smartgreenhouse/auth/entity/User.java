package com.smartgreenhouse.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "\"user\"") // ⚠️ 必须转义，否则报错
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password; // 实际生产需加密，Demo暂存明文

    private String role; // EXPERT, STANDARD, MINIMAL

    @Column(name = "default_mode")
    private String defaultMode; // 对应前端三种模式

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}