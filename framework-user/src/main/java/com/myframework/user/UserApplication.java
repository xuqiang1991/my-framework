package com.myframework.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.myframework.user", "com.myframework.common"})
@EnableDiscoveryClient
@MapperScan("com.myframework.user.mapper")
public class UserApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("""
                
                ===================================================
                     用户服务启动成功！
                     User Service Started Successfully!
                ===================================================
                """);
    }
}

