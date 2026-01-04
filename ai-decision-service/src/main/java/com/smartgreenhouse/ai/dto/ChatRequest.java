package com.smartgreenhouse.ai.dto;

import java.util.List;
import java.util.Map;

/**
 * 智慧问答请求
 */
public record ChatRequest(
        String prompt,                      // 用户问题
        List<Map<String, String>> history,  // 历史消息 [{"role":"user","content":"..."}, {"role":"assistant","content":"..."}]
        String greenhouseId                 // 大棚ID（可选，用于上下文）
) {}
