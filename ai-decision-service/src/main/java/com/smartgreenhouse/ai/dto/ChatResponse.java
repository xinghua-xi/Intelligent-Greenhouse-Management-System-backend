package com.smartgreenhouse.ai.dto;

/**
 * 智慧问答响应
 */
public record ChatResponse(
        boolean success,    // 是否成功
        String text,        // AI 回答
        String model,       // 使用的模型
        Long timestamp      // 响应时间戳
) {}
