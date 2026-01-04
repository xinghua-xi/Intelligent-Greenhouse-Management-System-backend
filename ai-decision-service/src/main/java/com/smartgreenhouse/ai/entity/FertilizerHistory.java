package com.smartgreenhouse.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 施肥建议历史记录实体
 */
@Data
@Entity
@Table(name = "fertilizer_history")
public class FertilizerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer week;

    @Column(name = "n_soil", precision = 10, scale = 2)
    private BigDecimal nSoil;

    @Column(name = "p_soil", precision = 10, scale = 2)
    private BigDecimal pSoil;

    @Column(name = "k_soil", precision = 10, scale = 2)
    private BigDecimal kSoil;

    @Column(precision = 4, scale = 2)
    private BigDecimal ph;

    @Column(precision = 4, scale = 2)
    private BigDecimal ec;

    @Column(precision = 4, scale = 2)
    private BigDecimal temp;

    @Column(name = "env_status", length = 50)
    private String envStatus;

    /**
     * AI 建议列表，存储为 JSONB
     */
    @Column(name = "advice_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String adviceJson;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
