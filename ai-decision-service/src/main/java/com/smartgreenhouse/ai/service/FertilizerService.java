package com.smartgreenhouse.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgreenhouse.ai.dto.FertilizerRequest;
import com.smartgreenhouse.ai.dto.FertilizerResponse;
import com.smartgreenhouse.ai.entity.FertilizerHistory;
import com.smartgreenhouse.ai.repository.FertilizerHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

/**
 * 番茄肥料配比服务 - 调用 Python AI 模型
 */
@Service
public class FertilizerService {

    private static final Logger log = LoggerFactory.getLogger(FertilizerService.class);

    private final RestClient restClient;
    private final FertilizerHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    public FertilizerService(
            RestClient.Builder builder,
            FertilizerHistoryRepository historyRepository,
            ObjectMapper objectMapper,
            @Value("${ai.python.base-url:http://localhost:8000}") String pythonBaseUrl) {
        this.restClient = builder.baseUrl(pythonBaseUrl).build();
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
        log.info("FertilizerService 初始化完成，Python 服务地址: {}", pythonBaseUrl);
    }

    /**
     * 调用 Python 模型获取精准施肥建议
     *
     * @param request 土壤和环境参数
     * @return 施肥建议响应
     */
    public FertilizerResponse getPreciseFertilizerPlan(FertilizerRequest request) {
        log.info("调用 AI 模型，请求参数: week={}, N={}, P={}, K={}, pH={}, EC={}, temp={}",
                request.week(), request.nSoil(), request.pSoil(), 
                request.kSoil(), request.ph(), request.ec(), request.temp());

        try {
            FertilizerResponse response = restClient.post()
                    .uri("/predict_precise")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(FertilizerResponse.class);

            log.info("AI 模型响应成功: status={}, envStatus={}", 
                    response != null ? response.status() : "null",
                    response != null ? response.envStatus() : "null");

            // 保存到数据库
            saveHistory(request, response);

            return response;
        } catch (Exception e) {
            log.error("AI 服务调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("肥料配比模型调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存施肥建议历史记录
     */
    private void saveHistory(FertilizerRequest request, FertilizerResponse response) {
        try {
            FertilizerHistory history = new FertilizerHistory();
            history.setWeek(request.week());
            history.setNSoil(BigDecimal.valueOf(request.nSoil()));
            history.setPSoil(BigDecimal.valueOf(request.pSoil()));
            history.setKSoil(BigDecimal.valueOf(request.kSoil()));
            history.setPh(BigDecimal.valueOf(request.ph()));
            history.setEc(BigDecimal.valueOf(request.ec()));
            history.setTemp(BigDecimal.valueOf(request.temp()));
            history.setEnvStatus(response.envStatus());
            
            // 将 adviceList 和 deficits 一起存为 JSON
            String adviceJson = objectMapper.writeValueAsString(new AdviceData(
                    response.adviceList(),
                    response.deficits()
            ));
            history.setAdviceJson(adviceJson);

            historyRepository.save(history);
            log.info("施肥建议已保存到数据库，id={}", history.getId());
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败: {}", e.getMessage());
        } catch (Exception e) {
            log.error("保存历史记录失败: {}", e.getMessage());
        }
    }

    /**
     * 用于 JSON 序列化的内部类
     */
    private record AdviceData(
            java.util.List<String> adviceList,
            java.util.Map<String, Double> deficits
    ) {}
}
