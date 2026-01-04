package com.smartgreenhouse.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 施肥历史记录实体（用于读取土壤数据）
 */
@Data
@Entity
@Table(name = "fertilizer_history")
public class FertilizerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer week;

    @Column(name = "n_soil")
    private BigDecimal nSoil;

    @Column(name = "p_soil")
    private BigDecimal pSoil;

    @Column(name = "k_soil")
    private BigDecimal kSoil;

    private BigDecimal ph;
    private BigDecimal ec;
    private BigDecimal temp;

    @Column(name = "env_status")
    private String envStatus;

    @Column(name = "advice_json", columnDefinition = "jsonb")
    private String adviceJson;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
