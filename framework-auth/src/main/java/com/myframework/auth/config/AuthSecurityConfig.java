package com.myframework.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 认证服务安全配置类
 * 提供密码编码器等安全相关的Bean
 */
@Configuration
public class AuthSecurityConfig {
    
    /**
     * 配置BCrypt密码编码器
     * 使用单例模式，避免重复创建实例，提高性能
     * BCrypt是一种基于Blowfish加密算法的密码哈希函数，
     * 具有自动加盐、计算成本可配置等特点，非常适合密码存储
     * 
     * @return BCrypt密码编码器实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

