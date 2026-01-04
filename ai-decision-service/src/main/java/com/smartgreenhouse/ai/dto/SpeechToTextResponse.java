package com.smartgreenhouse.ai.dto;

/**
 * 语音转文字响应
 */
public record SpeechToTextResponse(
        String text  // 识别出的文字
) {}
