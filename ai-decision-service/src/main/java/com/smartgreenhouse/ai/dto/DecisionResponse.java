package com.smartgreenhouse.ai.dto;

// 决策建议响应
public record DecisionResponse(
        String action,    // 建议动作: IRRIGATION, VENTILATION
        String reason,    // 原因: "土壤含水量低于阈值"
        Double confidence // 置信度: 0.91
) {}
