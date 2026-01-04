package com.smartgreenhouse.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.smartgreenhouse.ai", "com.smartgreenhouse.common.core"})
public class AiDecisionApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiDecisionApplication.class, args);
    }
}
