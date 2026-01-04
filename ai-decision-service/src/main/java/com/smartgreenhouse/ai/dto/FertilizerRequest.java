package com.smartgreenhouse.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 番茄肥料配比请求对象
 */
public record FertilizerRequest(
    int week,
    @JsonProperty("N_soil") double nSoil,
    @JsonProperty("P_soil") double pSoil,
    @JsonProperty("K_soil") double kSoil,
    double ph,
    double ec,
    double temp
) {}
