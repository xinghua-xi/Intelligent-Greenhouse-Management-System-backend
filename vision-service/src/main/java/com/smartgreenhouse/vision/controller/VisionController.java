package com.smartgreenhouse.vision.controller;

import com.smartgreenhouse.common.core.R;
import com.smartgreenhouse.vision.dto.DiagnosisRequest;
import com.smartgreenhouse.vision.dto.DiagnosisResponse;
import com.smartgreenhouse.vision.service.PlantDiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vision")
@RequiredArgsConstructor
public class VisionController {

    private final PlantDiagnosisService diagnosisService;

    /**
     * 病害识别接口
     * POST /vision/diagnosis
     */
    @PostMapping("/diagnosis")
    public R<DiagnosisResponse> diagnosePlant(@RequestBody DiagnosisRequest request) {
        DiagnosisResponse response = diagnosisService.diagnose(request);
        return R.ok(response);
    }
}