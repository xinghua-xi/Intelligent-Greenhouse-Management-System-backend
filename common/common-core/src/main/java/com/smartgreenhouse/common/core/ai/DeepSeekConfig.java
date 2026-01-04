package com.smartgreenhouse.common.core.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek 自动配置
 */
@Configuration
@ConditionalOnProperty(prefix = "deepseek", name = "api-key")
public class DeepSeekConfig {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Bean
    public DeepSeekClient deepSeekClient() {
        return new DeepSeekClient(apiKey, baseUrl);
    }
}
