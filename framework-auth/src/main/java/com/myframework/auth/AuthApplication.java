package com.myframework.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.myframework.auth", "com.myframework.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.myframework.api")
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

