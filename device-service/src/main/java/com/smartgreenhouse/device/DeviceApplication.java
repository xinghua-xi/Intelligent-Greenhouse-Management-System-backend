package com.smartgreenhouse.device;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DeviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeviceApplication.class, args);
    }

    // 初始化队列：设备控制指令队列
    @Bean
    public Queue commandQueue() {
        return new Queue("device.command.queue", true); // true表示持久化
    }
}
