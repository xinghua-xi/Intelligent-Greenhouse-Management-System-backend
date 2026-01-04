package com.smartgreenhouse.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 番茄肥料配比响应对象
 */
public record FertilizerResponse(
    String status,
    @JsonProperty("env_status") String envStatus,
    Map<String, Double> deficits,
    @JsonProperty("advice_list") List<String> adviceList
) {}
