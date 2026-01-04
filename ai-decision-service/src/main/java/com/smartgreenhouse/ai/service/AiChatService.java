package com.smartgreenhouse.ai.service;

import com.smartgreenhouse.ai.dto.ChatRequest;
import com.smartgreenhouse.ai.dto.ChatResponse;
import com.smartgreenhouse.common.core.ai.DeepSeekClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final DeepSeekClient deepSeekClient;

    /**
     * 系统人设 - 智能农业助手
     */
    private static final String SYSTEM_PROMPT = """
            你是智能农业助手"绿智云棚AI"。
            
            回答规则：
            1. 直接给出答案，不要铺垫和客套
            2. 每次回答控制在100字以内
            3. 用1-3个要点概括，不要长篇大论
            4. 使用纯文本，禁止使用 **、*、#、- 等符号
            5. 与农业无关的问题直接拒绝
            """;

    /**
     * 智慧问答（支持历史记录）
     */
    public ChatResponse chat(ChatRequest request) {
        log.info("收到问答请求: {}", request.prompt());

        // 构建用户消息
        StringBuilder userMessage = new StringBuilder(request.prompt());
        
        if (request.greenhouseId() != null && !request.greenhouseId().isEmpty()) {
            userMessage.append("\n\n[当前大棚ID: ").append(request.greenhouseId()).append("]");
        }

        // 调用 DeepSeek（带历史记录）
        String answer = deepSeekClient.chatWithHistory(
                SYSTEM_PROMPT, 
                userMessage.toString(), 
                request.history()
        );

        return new ChatResponse(
                true,
                answer,
                "deepseek-chat",
                System.currentTimeMillis()
        );
    }
}
