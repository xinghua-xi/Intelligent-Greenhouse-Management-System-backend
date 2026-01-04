package com.smartgreenhouse.ai.controller;

import com.smartgreenhouse.ai.dto.*;
import com.smartgreenhouse.ai.service.AiChatService;
import com.smartgreenhouse.ai.service.XunfeiSpeechService;
import com.smartgreenhouse.common.core.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class DecisionController {

    private final AiChatService aiChatService;
    private final XunfeiSpeechService speechService;

    /**
     * 智慧问答接口
     * POST /ai/chat
     */
    @PostMapping("/chat")
    public R<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.chat(request);
        return R.ok(response);
    }

    /**
     * 语音转文字接口
     * POST /ai/speech-to-text
     */
    @PostMapping("/speech-to-text")
    public R<SpeechToTextResponse> speechToText(@RequestBody SpeechToTextRequest request) {
        try {
            log.info("收到语音识别请求, 格式: {}", request.format());
            String text = speechService.speechToText(request.audio(), request.format());
            return R.ok(new SpeechToTextResponse(text));
        } catch (Exception e) {
            log.error("语音识别失败", e);
            return R.fail("语音识别失败: " + e.getMessage());
        }
    }

    /**
     * 获取 AI 托管建议
     * GET /ai/decision/recommend
     */
    @GetMapping("/decision/recommend")
    public R<DecisionResponse> getRecommendation() {
        DecisionResponse response = new DecisionResponse(
                "IRRIGATION",
                "检测到土壤含水量(28%)低于设定阈值(30%)，且未来2小时无降雨",
                0.92
        );
        return R.ok(response);
    }

    /**
     * 获取智能排产任务
     * GET /ai/schedule/tasks
     */
    @GetMapping("/schedule/tasks")
    public R<List<AiTask>> getScheduleTasks() {
        List<AiTask> tasks = List.of(
                new AiTask("task_01", "irrigation", "pending", 0.88),
                new AiTask("task_02", "fertilizer", "pending", 0.75),
                new AiTask("task_03", "ventilation", "completed", 0.95)
        );
        return R.ok(tasks);
    }
}