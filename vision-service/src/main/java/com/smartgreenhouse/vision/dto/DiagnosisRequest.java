package com.smartgreenhouse.vision.dto;

/**
 * 病虫害诊断请求
 * 支持两种方式：
 * 1. 文字描述症状
 * 2. 图片URL（需要配合支持视觉的模型）
 */
public record DiagnosisRequest(
        String description,  // 症状描述（推荐）
        String imageUrl,     // 图片URL（可选，当前DeepSeek不支持）
        String cropType      // 作物类型（可选，如：番茄、黄瓜）
) {}