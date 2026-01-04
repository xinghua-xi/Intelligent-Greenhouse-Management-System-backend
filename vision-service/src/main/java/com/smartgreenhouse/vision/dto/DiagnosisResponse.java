package com.smartgreenhouse.vision.dto;

import java.util.List;

public record DiagnosisResponse(
        String condition,   // 狀態: healthy, pest, disease
        String disease,     // 病害名稱: "紅蜘蛛", "晚疫病"
        Double confidence,  // 置信度: 0.93
        List<String> treatment // 建議措施
) {}
