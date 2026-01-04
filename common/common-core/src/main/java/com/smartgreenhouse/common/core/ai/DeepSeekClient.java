package com.smartgreenhouse.common.core.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API 客户端
 * 文档: https://platform.deepseek.com/api-docs
 */
@Slf4j
public class DeepSeekClient {

    private final String apiKey;
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepSeekClient(String apiKey) {
        this(apiKey, "https://api.deepseek.com");
    }

    public DeepSeekClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 发送聊天请求（支持历史记录）
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @param history 历史消息列表 (可选)
     * @param model 模型名称
     * @return AI 回复内容
     */
    public String chat(String systemPrompt, String userMessage, List<Map<String, String>> history, String model) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            List<Map<String, String>> messages = new ArrayList<>();
            
            // 1. 系统人设
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(Map.of("role", "system", "content", systemPrompt));
            }
            
            // 2. 历史记录
            if (history != null && !history.isEmpty()) {
                messages.addAll(history);
            }
            
            // 3. 当前用户消息
            messages.add(Map.of("role", "user", "content", userMessage));

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.5);      // 降低随机性，回答更精准
            body.put("max_tokens", 256);       // 限制最大输出长度

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            log.info("发送请求到 DeepSeek, 消息数: {}", messages.size());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            
            log.info("DeepSeek 回复成功, 长度: {}", content.length());
            return content;

        } catch (Exception e) {
            log.error("DeepSeek API 调用失败", e);
            throw new RuntimeException("AI 服务暂时不可用: " + e.getMessage());
        }
    }

    /**
     * 简单聊天（无历史记录）
     */
    public String chat(String systemPrompt, String userMessage, String model) {
        return chat(systemPrompt, userMessage, null, model);
    }

    /**
     * 使用 deepseek-chat 模型（无历史记录）
     */
    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage, null, "deepseek-chat");
    }

    /**
     * 使用 deepseek-chat 模型（带历史记录）
     */
    public String chatWithHistory(String systemPrompt, String userMessage, List<Map<String, String>> history) {
        return chat(systemPrompt, userMessage, history, "deepseek-chat");
    }
}
