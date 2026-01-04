package com.smartgreenhouse.ai.controller;

import com.smartgreenhouse.ai.dto.FertilizerRequest;
import com.smartgreenhouse.ai.dto.FertilizerResponse;
import com.smartgreenhouse.ai.service.FertilizerService;
import org.springframework.web.bind.annotation.*;

/**
 * 番茄肥料配比控制器
 */
@RestController
@RequestMapping("/api/fertilizer")
public class FertilizerController {

    private final FertilizerService fertilizerService;

    public FertilizerController(FertilizerService fertilizerService) {
        this.fertilizerService = fertilizerService;
    }

    /**
     * 分析土壤数据，获取精准施肥建议
     * 
     * POST /api/fertilizer/analyze
     */
    @PostMapping("/analyze")
    public FertilizerResponse analyze(@RequestBody FertilizerRequest request) {
        return fertilizerService.getPreciseFertilizerPlan(request);
    }
}
