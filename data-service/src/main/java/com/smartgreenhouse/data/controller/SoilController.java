package com.smartgreenhouse.data.controller;

import com.smartgreenhouse.common.core.R;
import com.smartgreenhouse.data.entity.FertilizerHistory;
import com.smartgreenhouse.data.repository.FertilizerHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 土壤数据接口
 */
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class SoilController {

    private final FertilizerHistoryRepository fertilizerHistoryRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 获取最新土壤数据
     * GET /data/soil
     */
    @GetMapping("/soil")
    public R<Map<String, Object>> getSoilData() {
        return fertilizerHistoryRepository.findTopByOrderByCreateTimeDesc()
                .map(history -> R.ok(convertToResponse(history)))
                .orElseGet(() -> R.ok(getDefaultSoilData()));
    }

    /**
     * 获取土壤历史数据（用于趋势图）
     * GET /data/soil/history?range=24h
     */
    @GetMapping("/soil/history")
    public R<List<Map<String, Object>>> getSoilHistory(
            @RequestParam(value = "range", defaultValue = "24h") String range) {
        
        LocalDateTime startTime = calculateStartTime(range);
        List<FertilizerHistory> historyList = fertilizerHistoryRepository
                .findByCreateTimeAfterOrderByCreateTimeAsc(startTime);

        if (!historyList.isEmpty()) {
            return R.ok(convertToHistoryResponse(historyList));
        }
        
        // 没有数据时返回模拟数据
        return R.ok(generateMockHistory(range));
    }

    private LocalDateTime calculateStartTime(String range) {
        LocalDateTime now = LocalDateTime.now();
        return switch (range) {
            case "1h" -> now.minusHours(1);
            case "7d" -> now.minusDays(7);
            case "30d" -> now.minusDays(30);
            default -> now.minusHours(24);
        };
    }

    private List<Map<String, Object>> convertToHistoryResponse(List<FertilizerHistory> historyList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (FertilizerHistory h : historyList) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("time", h.getCreateTime().format(TIME_FORMATTER));
            point.put("week", h.getWeek());
            point.put("N_soil", h.getNSoil() != null ? h.getNSoil().doubleValue() : 0);
            point.put("P_soil", h.getPSoil() != null ? h.getPSoil().doubleValue() : 0);
            point.put("K_soil", h.getKSoil() != null ? h.getKSoil().doubleValue() : 0);
            point.put("ph", h.getPh() != null ? h.getPh().doubleValue() : 0);
            point.put("ec", h.getEc() != null ? h.getEc().doubleValue() : 0);
            point.put("temp", h.getTemp() != null ? h.getTemp().doubleValue() : 0);
            result.add(point);
        }
        return result;
    }

    /**
     * 生成模拟历史数据
     */
    private List<Map<String, Object>> generateMockHistory(String range) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random(42);

        int hours = switch (range) {
            case "1h" -> 1;
            case "7d" -> 168;
            case "30d" -> 720;
            default -> 24;
        };
        int intervalMinutes = switch (range) {
            case "1h" -> 10;
            case "7d" -> 360;
            case "30d" -> 1440;
            default -> 180;
        };

        int points = (hours * 60) / intervalMinutes;
        for (int i = points - 1; i >= 0; i--) {
            LocalDateTime time = now.minusMinutes((long) i * intervalMinutes);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("time", time.format(TIME_FORMATTER));
            point.put("week", 5);
            point.put("N_soil", 100 + random.nextDouble() * 40);
            point.put("P_soil", 25 + random.nextDouble() * 20);
            point.put("K_soil", 150 + random.nextDouble() * 60);
            point.put("ph", 6.0 + random.nextDouble() * 1.0);
            point.put("ec", 1.5 + random.nextDouble() * 1.0);
            point.put("temp", 22 + random.nextDouble() * 6);
            result.add(point);
        }
        return result;
    }

    private Map<String, Object> convertToResponse(FertilizerHistory history) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("week", history.getWeek() != null ? history.getWeek() : 1);
        data.put("N_soil", history.getNSoil() != null ? history.getNSoil().doubleValue() : 0);
        data.put("P_soil", history.getPSoil() != null ? history.getPSoil().doubleValue() : 0);
        data.put("K_soil", history.getKSoil() != null ? history.getKSoil().doubleValue() : 0);
        data.put("ph", history.getPh() != null ? history.getPh().doubleValue() : 7.0);
        data.put("ec", history.getEc() != null ? history.getEc().doubleValue() : 1.5);
        data.put("temp", history.getTemp() != null ? history.getTemp().doubleValue() : 25.0);
        return data;
    }

    /**
     * 默认土壤数据（数据库无记录时返回）
     */
    private Map<String, Object> getDefaultSoilData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("week", 1);
        data.put("N_soil", 100.0);
        data.put("P_soil", 30.0);
        data.put("K_soil", 150.0);
        data.put("ph", 6.5);
        data.put("ec", 1.8);
        data.put("temp", 25.0);
        return data;
    }
}
