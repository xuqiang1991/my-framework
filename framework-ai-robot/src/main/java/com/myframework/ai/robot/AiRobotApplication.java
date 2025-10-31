package com.myframework.ai.robot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AI机器人服务启动类
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.myframework.ai.robot.mapper")
public class AiRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiRobotApplication.class, args);
        System.out.println("===================================");
        System.out.println("AI机器人服务启动成功！");
        System.out.println("===================================");
    }
}

