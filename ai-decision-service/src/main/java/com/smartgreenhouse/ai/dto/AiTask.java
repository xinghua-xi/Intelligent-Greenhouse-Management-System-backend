package com.smartgreenhouse.ai.dto;

// 智能排产任务
public record AiTask(
        String id,
        String type,      // irrigation, fertilizer
        String status,    // pending, completed
        Double aiConfidence
) {}