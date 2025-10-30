package com.myframework.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户服务安全配置类
 * 提供密码编码器、权限拦截器等安全相关的Bean
 */
@Configuration
public class UserSecurityConfig implements WebMvcConfigurer {
    
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
    
    /**
     * 配置Sa-Token拦截器
     * 注册路由拦截器，用于权限验证
     * 
     * 注意：用户服务的权限验证暂时禁用，因为token是由认证服务颁发的OAuth2 token
     * TODO: 实现OAuth2 token验证机制
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 暂时注释掉 Sa-Token 拦截器，因为用户服务需要验证认证服务颁发的OAuth2 token
        // 而不是 Sa-Token 的session token
        
        /* 
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 在此处可以添加全局的权限验证逻辑
            // 具体的权限验证由 @SaCheckRole 注解控制
        }))
        .addPathPatterns("/**")  // 拦截所有路径
        .excludePathPatterns(
            // 排除不需要权限验证的路径
            "/user/register/self",           // 用户自助注册
            "/user/register/check-username",  // 检查用户名
            "/captcha/generate",              // 获取验证码
            "/api/third-party/**",            // 第三方API（有自己的验证机制）
            "/error",                         // 错误页面
            "/doc.html",                      // API文档
            "/swagger-resources/**",          // Swagger资源
            "/v3/api-docs/**",                // OpenAPI文档
            "/webjars/**",                    // Webjars资源
            "/favicon.ico",                   // 图标
            "/*.html",                        // 静态HTML页面
            "/static/**"                      // 静态资源
        );
        */
    }
}

