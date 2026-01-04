package com.smartgreenhouse.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;           // 文章标题
    
    @Column(columnDefinition = "TEXT")
    private String content;         // 文章内容
    
    private String category;        // 分类: PEST, DISEASE, PLANTING, MANAGEMENT
    
    @Column(name = "crop_type")
    private String cropType;        // 作物类型: 番茄, 黄瓜, 草莓
    
    private String author;          // 作者
    
    @Column(name = "view_count")
    private Integer viewCount = 0;  // 浏览量
    
    @Column(name = "cover_image")
    private String coverImage;      // 封面图URL
    
    private String tags;            // 标签，逗号分隔
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
