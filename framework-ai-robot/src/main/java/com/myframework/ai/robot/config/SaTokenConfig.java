package com.myframework.ai.robot.config;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myframework.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * Sa-Token配置类
 * 配置OAuth2 token验证拦截器
 * 
 * @author MyFramework
 * @since 2025-01-30
 */
@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Value("${ai-robot.auth.server-url:http://auth:8081}")
    private String authServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 注册OAuth2 token验证拦截器
     * 用于验证OAuth2 access token并注入用户ID到Sa-Token上下文
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册OAuth2 token验证拦截器，并注入到Sa-Token上下文
        registry.addInterceptor(new AsyncHandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull Object handler) {
                try {
                    // 从请求头中获取Bearer token
                    String authorization = request.getHeader("Authorization");
                    
                    if (authorization == null || !authorization.startsWith("Bearer ")) {
                        log.warn("请求缺少Authorization头或格式错误");
                        throw new RuntimeException("未登录或token无效");
                    }
                    
                    // 提取token
                    String accessToken = authorization.substring(7);
                    
                    // 验证OAuth2 token
                    String userId = validateOAuth2Token(accessToken);
                    
                    // 将userId注入到Sa-Token上下文中
                    // 使用临时登录，避免创建实际的session
                    StpUtil.login(userId, "OAUTH2");
                    
                    log.debug("OAuth2 token验证成功，用户已登录: userId={}", userId);
                    
                    return true;
                } catch (Exception e) {
                    log.error("OAuth2 token验证失败: {}", e.getMessage(), e);
                    throw e;
                }
            }
        })
        .addPathPatterns("/api/ai/**")  // 拦截所有AI API
        .excludePathPatterns(
            // 排除不需要认证的路径
            "/api/ai/robot/public",  // 公开的机器人列表
            "/error",                // 错误页面
            "/doc.html",             // API文档
            "/swagger-resources/**", // Swagger资源
            "/v3/api-docs/**",       // OpenAPI文档
            "/webjars/**",           // Webjars资源
            "/favicon.ico",          // 图标
            "/*.html",               // 静态HTML页面
            "/static/**",            // 静态资源
            "/actuator/**"           // 监控端点
        );
    }

    /**
     * 验证OAuth2 access token
     * 通过调用auth服务的introspect接口验证token
     * 
     * @param accessToken OAuth2 access token
     * @return 用户ID
     */
    private String validateOAuth2Token(String accessToken) {
        try {
            // 构建请求URL
            String url = authServerUrl + "/oauth2/introspect";
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // 设置请求参数
            Map<String, String> params = new HashMap<>();
            params.put("token", accessToken);
            
            // 构建请求体
            StringBuilder bodyBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (bodyBuilder.length() > 0) {
                    bodyBuilder.append("&");
                }
                bodyBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            HttpEntity<String> request = new HttpEntity<>(bodyBuilder.toString(), headers);
            
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Result<?> result = objectMapper.readValue(response.getBody(), Result.class);
                
                if (result.getCode() == 200 && result.getData() != null) {
                    // 获取OAuthToken对象
                    Map<String, Object> tokenData = (Map<String, Object>) result.getData();
                    String userId = (String) tokenData.get("userId");
                    
                    if (userId != null) {
                        log.debug("OAuth2 token验证成功: userId={}", userId);
                        return userId;
                    }
                }
            }
            
            log.warn("OAuth2 token验证失败");
            throw new RuntimeException("Token验证失败");
            
        } catch (Exception e) {
            log.error("验证OAuth2 token时发生异常: {}", e.getMessage(), e);
            throw new RuntimeException("Token验证异常: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 构建请求URL
        String url = "http://localhost:8081/oauth2/introspect";

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 设置请求参数
        Map<String, String> params = new HashMap<>();
        params.put("token", "123");

        // 构建请求体
        StringBuilder bodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (bodyBuilder.length() > 0) {
                bodyBuilder.append("&");
            }
            bodyBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }

        HttpEntity<String> request = new HttpEntity<>(bodyBuilder.toString(), headers);

        // 发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        log.info(response.getBody());
    }
}

