package com.myframework.ai.robot.config;

// TODO: 临时注释Spring AI配置，等API稳定后再启用
// import org.springframework.ai.openai.OpenAiChatModel;
// import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置（临时禁用）
 * TODO: 等Spring AI 1.0.0正式版发布后再启用
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Configuration
public class SpringAiConfig {

    /* 临时注释，等Spring AI API稳定后再启用
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiChatModel(openAiApi);
    }
    */
}

