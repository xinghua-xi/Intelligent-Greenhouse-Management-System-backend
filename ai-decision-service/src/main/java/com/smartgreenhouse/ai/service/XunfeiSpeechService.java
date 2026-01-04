package com.smartgreenhouse.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 讯飞语音识别服务
 * 使用 WebSocket 实时语音转写 API
 * 支持 M4A/AAC 自动转码为 PCM
 */
@Slf4j
@Service
public class XunfeiSpeechService {

    @Value("${xunfei.appid}")
    private String appId;

    @Value("${xunfei.api-key}")
    private String apiKey;

    @Value("${xunfei.api-secret}")
    private String apiSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * 语音转文字
     * @param audioBase64 Base64 编码的音频数据
     * @param format 音频格式 (wav, pcm, m4a, aac, mp3)
     * @return 识别出的文字
     */
    public String speechToText(String audioBase64, String format) throws Exception {
        // 1. 解码 Base64 音频
        byte[] rawAudioData = Base64.getDecoder().decode(audioBase64);
        log.info("原始音频大小: {} bytes, 格式: {}", rawAudioData.length, format);

        // 2. 如果不是 PCM/WAV 格式，需要转码
        final byte[] audioData;
        if (needsConversion(format)) {
            log.info("检测到 {} 格式，开始转码为 PCM...", format);
            audioData = convertToPcm(rawAudioData, format);
            log.info("转码完成，PCM 数据大小: {} bytes", audioData.length);
        } else {
            audioData = rawAudioData;
        }

        // 3. 构建 WebSocket URL
        String url = buildAuthUrl();
        log.info("讯飞 WebSocket URL 已生成");

        // 4. 使用 CompletableFuture 等待结果
        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        StringBuilder resultText = new StringBuilder();

        // 5. 创建 WebSocket 连接
        Request request = new Request.Builder().url(url).build();
        
        WebSocket webSocket = httpClient.newWebSocket(request, new WebSocketListener() {
            private int status = 0; // 0: 第一帧, 1: 中间帧, 2: 最后一帧

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("WebSocket 连接已建立");
                // 开始发送音频数据
                sendAudioData(webSocket, audioData, format);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JsonNode root = objectMapper.readTree(text);
                    int code = root.path("code").asInt();
                    
                    if (code != 0) {
                        String message = root.path("message").asText();
                        log.error("讯飞返回错误: code={}, message={}", code, message);
                        resultFuture.completeExceptionally(new RuntimeException("讯飞错误: " + message));
                        webSocket.close(1000, "Error");
                        return;
                    }

                    // 解析识别结果
                    JsonNode data = root.path("data");
                    JsonNode result = data.path("result");
                    JsonNode ws = result.path("ws");
                    
                    if (ws.isArray()) {
                        for (JsonNode w : ws) {
                            JsonNode cw = w.path("cw");
                            if (cw.isArray()) {
                                for (JsonNode c : cw) {
                                    String word = c.path("w").asText();
                                    resultText.append(word);
                                }
                            }
                        }
                    }

                    // 检查是否结束
                    int statusCode = data.path("status").asInt();
                    if (statusCode == 2) {
                        log.info("识别完成, 结果: {}", resultText);
                        resultFuture.complete(resultText.toString());
                        webSocket.close(1000, "Complete");
                    }
                } catch (Exception e) {
                    log.error("解析讯飞响应失败", e);
                    resultFuture.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("WebSocket 连接失败", t);
                resultFuture.completeExceptionally(t);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.info("WebSocket 已关闭: code={}, reason={}", code, reason);
                if (!resultFuture.isDone()) {
                    resultFuture.complete(resultText.toString());
                }
            }
        });

        // 5. 等待结果（最多60秒）
        try {
            return resultFuture.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            webSocket.close(1000, "Timeout");
            throw e;
        }
    }

    /**
     * 发送音频数据
     */
    private void sendAudioData(WebSocket webSocket, byte[] audioData, String format) {
        try {
            int frameSize = 1280; // 每帧大小
            int offset = 0;
            int status = 0; // 0: 第一帧, 1: 中间帧, 2: 最后一帧

            while (offset < audioData.length) {
                int end = Math.min(offset + frameSize, audioData.length);
                byte[] frame = Arrays.copyOfRange(audioData, offset, end);
                
                // 判断帧状态
                if (offset == 0) {
                    status = 0; // 第一帧
                } else if (end >= audioData.length) {
                    status = 2; // 最后一帧
                } else {
                    status = 1; // 中间帧
                }

                // 构建请求 JSON
                String frameData = buildFrameData(frame, status, format);
                webSocket.send(frameData);

                offset = end;

                // 控制发送速率，模拟实时音频流
                if (status != 2) {
                    Thread.sleep(40); // 40ms 间隔
                }
            }
            
            log.info("音频数据发送完成");
        } catch (Exception e) {
            log.error("发送音频数据失败", e);
        }
    }

    /**
     * 构建帧数据
     */
    private String buildFrameData(byte[] audio, int status, String format) throws Exception {
        Map<String, Object> frame = new HashMap<>();
        
        // common 参数（仅第一帧需要）
        if (status == 0) {
            Map<String, Object> common = new HashMap<>();
            common.put("app_id", appId);
            frame.put("common", common);

            // business 参数（仅第一帧需要）
            Map<String, Object> business = new HashMap<>();
            business.put("language", "zh_cn");
            business.put("domain", "iat");
            business.put("accent", "mandarin");
            business.put("vad_eos", 3000); // 静音检测时间
            business.put("dwa", "wpgs"); // 动态修正
            frame.put("business", business);
        }

        // data 参数
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        data.put("format", "audio/L16;rate=16000");
        data.put("encoding", "raw");
        data.put("audio", Base64.getEncoder().encodeToString(audio));
        frame.put("data", data);

        return objectMapper.writeValueAsString(frame);
    }

    /**
     * 构建鉴权 URL
     */
    private String buildAuthUrl() throws Exception {
        String host = "iat-api.xfyun.cn";
        String path = "/v2/iat";
        String url = "wss://" + host + path;

        // 生成 RFC1123 格式的时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());

        // 构建签名原文
        String signatureOrigin = "host: " + host + "\n" +
                "date: " + date + "\n" +
                "GET " + path + " HTTP/1.1";

        // HMAC-SHA256 签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] signatureBytes = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(signatureBytes);

        // 构建 authorization
        String authorizationOrigin = String.format(
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                apiKey, signature
        );
        String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        // 构建最终 URL
        return url + "?" +
                "authorization=" + URLEncoder.encode(authorization, StandardCharsets.UTF_8) +
                "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8) +
                "&host=" + URLEncoder.encode(host, StandardCharsets.UTF_8);
    }

    /**
     * 判断是否需要转码
     */
    private boolean needsConversion(String format) {
        if (format == null) return false;
        String lowerFormat = format.toLowerCase();
        return lowerFormat.contains("m4a") || 
               lowerFormat.contains("aac") || 
               lowerFormat.contains("mp3") ||
               lowerFormat.contains("ogg") ||
               lowerFormat.contains("webm");
    }

    /**
     * 使用 FFmpeg 将音频转换为 PCM 格式
     * PCM: 16kHz, 16bit, 单声道
     */
    private byte[] convertToPcm(byte[] inputAudio, String format) throws Exception {
        Path tempDir = Files.createTempDirectory("audio_convert");
        Path inputFile = tempDir.resolve("input." + getExtension(format));
        Path outputFile = tempDir.resolve("output.pcm");

        try {
            // 写入临时输入文件
            Files.write(inputFile, inputAudio);

            // 构建 FFmpeg 命令
            // -i: 输入文件
            // -f s16le: 输出格式为 16bit little-endian PCM
            // -acodec pcm_s16le: 音频编码
            // -ar 16000: 采样率 16kHz
            // -ac 1: 单声道
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", inputFile.toString(),
                    "-f", "s16le",
                    "-acodec", "pcm_s16le",
                    "-ar", "16000",
                    "-ac", "1",
                    "-y",  // 覆盖输出文件
                    outputFile.toString()
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 读取 FFmpeg 输出（用于调试）
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg 转码失败，退出码: " + exitCode);
            }

            // 读取转换后的 PCM 数据
            return Files.readAllBytes(outputFile);

        } finally {
            // 清理临时文件
            try {
                Files.deleteIfExists(inputFile);
                Files.deleteIfExists(outputFile);
                Files.deleteIfExists(tempDir);
            } catch (Exception e) {
                log.warn("清理临时文件失败", e);
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String format) {
        if (format == null) return "wav";
        String lower = format.toLowerCase();
        if (lower.contains("m4a")) return "m4a";
        if (lower.contains("aac")) return "aac";
        if (lower.contains("mp3")) return "mp3";
        if (lower.contains("ogg")) return "ogg";
        if (lower.contains("webm")) return "webm";
        if (lower.contains("wav")) return "wav";
        return "wav";
    }
}
