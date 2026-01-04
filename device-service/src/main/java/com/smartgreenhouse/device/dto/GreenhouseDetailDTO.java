package com.smartgreenhouse.device.dto;

import com.smartgreenhouse.device.entity.Greenhouse;
import com.smartgreenhouse.device.entity.Zone;
import com.smartgreenhouse.device.entity.Actuator;
import lombok.Data;
import java.util.List;

@Data
public class GreenhouseDetailDTO {
    private Greenhouse info;
    private List<ZoneWithDevices> zones;

    @Data
    public static class ZoneWithDevices {
        private Zone zone;
        private List<Actuator> devices;
    }
}