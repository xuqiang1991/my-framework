package com.myframework.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.myframework.auth", "com.myframework.common"})
@EnableDiscoveryClient
@EnableDubbo
public class AuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("""
                
                ===================================================
                     认证服务启动成功！
                     Auth Service Started Successfully!
                ===================================================
                """);
    }
}

