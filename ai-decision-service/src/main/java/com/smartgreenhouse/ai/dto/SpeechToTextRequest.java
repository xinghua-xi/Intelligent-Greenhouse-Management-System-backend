package com.smartgreenhouse.ai.dto;

/**
 * 语音转文字请求
 */
public record SpeechToTextRequest(
        String audio,   // Base64 编码的音频数据
        String format   // 音频格式: wav, pcm, mp3
) {}
