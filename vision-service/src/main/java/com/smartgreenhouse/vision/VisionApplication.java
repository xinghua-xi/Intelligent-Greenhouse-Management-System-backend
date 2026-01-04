package com.smartgreenhouse.vision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.smartgreenhouse.vision", "com.smartgreenhouse.common.core"})
public class VisionApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisionApplication.class, args);
    }
}