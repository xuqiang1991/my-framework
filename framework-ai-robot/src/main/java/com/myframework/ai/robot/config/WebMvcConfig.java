package com.myframework.ai.robot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置静态资源和欢迎页
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置欢迎页
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 设置根路径重定向到index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

