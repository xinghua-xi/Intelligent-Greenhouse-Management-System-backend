package com.smartgreenhouse.device.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point; // ⚠️ 注意包名
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "greenhouse")
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;       // 例如：1号温室
    private String crop;       // 作物：樱桃番茄
    private String status;     // 状态：NORMAL, WARNING

    @Column(name = "health_score")
    private Integer healthScore;

    // ✨ 核心：PostGIS 空间数据映射
    // columnDefinition 明确告诉数据库这是空间类型
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
