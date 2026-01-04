package com.smartgreenhouse.vision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgreenhouse.common.core.ai.DeepSeekClient;
import com.smartgreenhouse.vision.dto.DiagnosisRequest;
import com.smartgreenhouse.vision.dto.DiagnosisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantDiagnosisService {

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
            你是一个专业的植物病虫害诊断专家。用户会描述植物的症状，请你：
            1. 根据描述分析可能的病害或虫害
            2. 给出诊断结果和防治建议
            
            请严格按照以下JSON格式返回结果（不要返回其他内容）：
            {
                "condition": "healthy/pest/disease",
                "disease": "病害或虫害名称",
                "confidence": 0.85,
                "treatment": ["建议措施1", "建议措施2", "建议措施3"]
            }
            
            condition 取值说明：
            - healthy: 植物健康，无明显病虫害
            - pest: 虫害（如蚜虫、红蜘蛛、白粉虱等）
            - disease: 病害（如霜霉病、灰霉病、病毒病等）
            
            注意：
            - confidence 为置信度，0-1之间
            - 如果描述不够详细，confidence 设为较低值并在 treatment 中建议提供更多信息
            - treatment 至少给出2-3条具体可行的建议
            """;

    /**
     * 分析植物症状，诊断病虫害
     */
    public DiagnosisResponse diagnose(DiagnosisRequest request) {
        // 构建用户消息
        StringBuilder userMessage = new StringBuilder();
        
        if (request.cropType() != null && !request.cropType().isEmpty()) {
            userMessage.append("作物类型：").append(request.cropType()).append("\n");
        }
        
        if (request.description() != null && !request.description().isEmpty()) {
            userMessage.append("症状描述：").append(request.description());
        } else if (request.imageUrl() != null && !request.imageUrl().isEmpty()) {
            // 如果只有图片URL，提示用户描述症状
            return new DiagnosisResponse(
                    "unknown",
                    "需要症状描述",
                    0.0,
                    List.of(
                            "当前版本暂不支持图片识别，请描述植物症状",
                            "例如：叶片发黄、有斑点、卷曲、有虫子等",
                            "描述越详细，诊断越准确"
                    )
            );
        } else {
            return new DiagnosisResponse(
                    "unknown",
                    "缺少症状信息",
                    0.0,
                    List.of("请提供植物症状描述")
            );
        }

        log.info("开始诊断, 症状: {}", userMessage);

        try {
            // 调用 DeepSeek
            String response = deepSeekClient.chat(SYSTEM_PROMPT, userMessage.toString());
            log.info("DeepSeek 返回: {}", response);

            // 解析 JSON 响应
            return parseResponse(response);

        } catch (Exception e) {
            log.error("病虫害诊断失败", e);
            return new DiagnosisResponse(
                    "error",
                    "诊断服务异常",
                    0.0,
                    List.of("AI 服务暂时不可用，请稍后重试", "或联系农业专家进行人工诊断")
            );
        }
    }

    /**
     * 解析 AI 返回的 JSON
     */
    private DiagnosisResponse parseResponse(String response) {
        try {
            String json = extractJson(response);
            JsonNode root = objectMapper.readTree(json);

            String condition = root.path("condition").asText("unknown");
            String disease = root.path("disease").asText(null);
            double confidence = root.path("confidence").asDouble(0.5);

            List<String> treatment = new ArrayList<>();
            JsonNode treatmentNode = root.path("treatment");
            if (treatmentNode.isArray()) {
                for (JsonNode node : treatmentNode) {
                    treatment.add(node.asText());
                }
            }

            return new DiagnosisResponse(condition, disease, confidence, treatment);

        } catch (Exception e) {
            log.warn("解析 AI 响应失败: {}", e.getMessage());
            return new DiagnosisResponse(
                    "unknown",
                    "解析失败",
                    0.5,
                    List.of(response)  // 返回原始文本
            );
        }
    }

    private String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}
