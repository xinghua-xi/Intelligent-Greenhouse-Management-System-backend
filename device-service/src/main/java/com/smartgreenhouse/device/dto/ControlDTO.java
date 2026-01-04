package com.smartgreenhouse.device.dto;
// 接收前端控制指令：模式、动作、持续时长
public record ControlDTO(String mode, String action, Integer duration) {}
