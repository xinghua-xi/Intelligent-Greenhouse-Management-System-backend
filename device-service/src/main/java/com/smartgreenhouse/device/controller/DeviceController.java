package com.smartgreenhouse.device.controller;

import com.smartgreenhouse.common.core.R;
import com.smartgreenhouse.device.dto.ControlDTO;
import com.smartgreenhouse.device.dto.GreenhouseDetailDTO;
import com.smartgreenhouse.device.entity.Actuator;
import com.smartgreenhouse.device.entity.Greenhouse;
import com.smartgreenhouse.device.entity.Node;
import com.smartgreenhouse.device.entity.Zone;
import com.smartgreenhouse.device.repository.ActuatorRepository;
import com.smartgreenhouse.device.repository.GreenhouseRepository;
import com.smartgreenhouse.device.repository.NodeRepository;
import com.smartgreenhouse.device.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final GreenhouseRepository greenhouseRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ZoneRepository zoneRepository;
    private final ActuatorRepository actuatorRepository;
    private final NodeRepository nodeRepository;

    // 1. 获取所有大棚列表
    @GetMapping("/greenhouses")
    public R<List<Greenhouse>> listGreenhouses() {
        return R.ok(greenhouseRepository.findAll());
    }

    // 2. 发送设备控制指令 (异步)
    @PostMapping("/{deviceId}/control")
    public R<String> controlDevice(@PathVariable("deviceId") String deviceId, @RequestBody ControlDTO cmd) {
        // 构建消息包
        Map<String, Object> message = new HashMap<>();
        message.put("deviceId", deviceId);
        message.put("action", cmd.action());    // IRRIGATION (灌溉)
        message.put("duration", cmd.duration());
        message.put("timestamp", LocalDateTime.now().toString());

        // ✨ 发送到 RabbitMQ 队列
        rabbitTemplate.convertAndSend("device.command.queue", message);

        return R.ok("指令已发送");
    }
    /**
     * 获取大棚详情（包含分区和设备状态）
     * 用于 Web端 3D 数字孪生 和 App端 详情页
     * GET /devices/greenhouses/{id}/detail
     */
    @GetMapping("/greenhouses/{id}/detail")
    public R<GreenhouseDetailDTO> getGreenhouseDetail(@PathVariable("id") String id) {
        // 1. 查大棚基本信息
        Greenhouse greenhouse = greenhouseRepository.findById(id).orElseThrow();

        // 2. 查该大棚下的所有分区
        List<Zone> zones = zoneRepository.findByGreenhouseId(id);

        // 3. 组装数据
        GreenhouseDetailDTO result = new GreenhouseDetailDTO();
        result.setInfo(greenhouse);

        List<GreenhouseDetailDTO.ZoneWithDevices> zoneList = zones.stream().map(zone -> {
            GreenhouseDetailDTO.ZoneWithDevices item = new GreenhouseDetailDTO.ZoneWithDevices();
            item.setZone(zone);
            // 查该分区下的设备
            item.setDevices(actuatorRepository.findByZoneId(zone.getId()));
            return item;
        }).toList();

        result.setZones(zoneList);
        return R.ok(result);
    }

    /**
     * 获取节点状态列表
     * GET /devices/nodes?greenhouseId=gh_001
     */
    @GetMapping("/nodes")
    public R<List<Node>> listNodes(@RequestParam(value = "greenhouseId", required = false) String greenhouseId) {
        if (greenhouseId != null) {
            return R.ok(nodeRepository.findByGreenhouseId(greenhouseId));
        }
        return R.ok(nodeRepository.findAll());
    }

}
