package com.myframework.ai.robot.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 用于OAuth2授权后的Sa-Token登录注入
 * 
 * @author MyFramework
 * @since 2025-01-30
 */
@Slf4j
@Tag(name = "认证管理", description = "OAuth2认证相关接口")
@RestController
@RequestMapping("/api/ai/auth")
public class AuthController {

    @Value("${ai-robot.auth.server-url:http://auth:8081}")
    private String authServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // OAuth2 token 过期时间格式
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * OAuth2授权登录后注入到Sa-Token
     * 验证OAuth2 token并注入到Sa-Token，然后调用userinfo接口获取完整用户信息并返回给前端
     * 
     * @param request HTTP请求对象，用于获取Authorization头
     * @return 登录结果和用户信息
     */
    @Operation(summary = "OAuth2授权登录后注入到Sa-Token", description = "授权成功后调用此接口，验证OAuth2 token并注入到Sa-Token，然后返回用户信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(HttpServletRequest request) {
        try {
            // 从请求头获取OAuth2 access token
            String authorization = request.getHeader("Authorization");
            
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.warn("请求缺少Authorization头或格式错误");
                return Result.error("未登录或token无效");
            }
            
            // 提取token
            String accessToken = authorization.substring(7);
            
            // 验证OAuth2 token并获取用户ID和过期时间
            OAuthTokenInfo tokenInfo = validateOAuth2Token(accessToken);
            String userId = tokenInfo.getUserId();
            long expireTime = tokenInfo.getExpireTime();
            
            // 将用户信息注入到Sa-Token
            loginToSaToken(userId, expireTime);
            
            // 获取登录token（Sa-Token的token）
            String saToken = StpUtil.getTokenValue();
            
            log.info("OAuth2授权登录成功，用户已注入到Sa-Token: userId={}, token={}", userId, saToken);
            
            // 调用userinfo接口获取完整用户信息
            UserDTO userInfo = null;
            try {
                userInfo = getUserInfoFromAuthServer(accessToken);
                log.info("获取用户信息成功: userId={}, username={}", userInfo.getUserId(), userInfo.getUsername());
            } catch (Exception e) {
                log.warn("获取用户信息失败: {}", e.getMessage());
                // 获取用户信息失败不影响主流程，继续执行
            }
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("token", saToken);
            data.put("isLogin", StpUtil.isLogin());
            
            // 如果成功获取用户信息，添加到返回数据中
            if (userInfo != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", userInfo.getUserId());
                userData.put("username", userInfo.getUsername());
                userData.put("nickname", userInfo.getNickname());
                userData.put("realName", userInfo.getRealName());
                userData.put("phone", userInfo.getPhone());
                userData.put("email", userInfo.getEmail());
                userData.put("gender", userInfo.getGender());
                userData.put("avatar", userInfo.getAvatar());
                userData.put("platformId", userInfo.getPlatformId());
                userData.put("status", userInfo.getStatus());
                data.put("userInfo", userData);
            }
            
            return Result.success("登录成功，用户信息已注入到Sa-Token", data);
        } catch (Exception e) {
            log.error("注入Sa-Token失败: {}", e.getMessage(), e);
            return Result.error("注入Sa-Token失败: " + e.getMessage());
        }
    }
    
    /**
     * 从auth服务获取用户信息
     * 
     * @param accessToken OAuth2 access token
     * @return 用户信息
     */
    private UserDTO getUserInfoFromAuthServer(String accessToken) {
        try {
            // 构建请求URL
            String url = authServerUrl + "/oauth2/userinfo";
            
            // 设置请求头（GET请求不需要Content-Type）
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            // 发送GET请求
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
            );
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Result<?> result = objectMapper.readValue(response.getBody(), Result.class);
                
                if (result.getCode() == 200 && result.getData() != null) {
                    // 将data转换为UserDTO
                    String userJson = objectMapper.writeValueAsString(result.getData());
                    UserDTO userDTO = objectMapper.readValue(userJson, UserDTO.class);
                    return userDTO;
                }
            }
            
            log.warn("获取用户信息失败: 响应状态码={}", response.getStatusCode());
            throw new RuntimeException("获取用户信息失败");
            
        } catch (Exception e) {
            log.error("调用userinfo接口时发生异常: {}", e.getMessage(), e);
            throw new RuntimeException("获取用户信息异常: " + e.getMessage());
        }
    }

    /**
     * 验证OAuth2 access token
     * 通过调用auth服务的introspect接口验证token
     * 
     * @param accessToken OAuth2 access token
     * @return OAuth2 token信息（包含用户ID和过期时间）
     */
    private OAuthTokenInfo validateOAuth2Token(String accessToken) {
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
                        // 获取过期时间
                        long expireTime = getExpireTime(tokenData);
                        log.debug("OAuth2 token验证成功: userId={}, expireTime={}秒", userId, expireTime);
                        return new OAuthTokenInfo(userId, expireTime);
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
    
    /**
     * 将用户登录到Sa-Token系统
     * 使用userId作为device标识，保持登录状态
     * Sa-Token会自动处理重复登录的情况（如果已登录会先踢下线，然后重新登录）
     * 
     * @param userId 用户ID
     * @param expireTime 过期时间（秒）
     */
    private void loginToSaToken(String userId, long expireTime) {
        try {
            // 将userId注入到Sa-Token上下文中，使用userId作为device（设备标识）
            SaLoginModel saLoginModel = new SaLoginModel();
            saLoginModel.setDevice(userId);
            if (expireTime > 0) {
                saLoginModel.setTimeout(expireTime);
            }
            
            // 执行登录（如果已登录会先踢下线，然后重新登录）
            // 这样可以确保每次请求时都保持最新的登录状态
            StpUtil.login(userId, saLoginModel);
            
            // 记录登录日志
            if (expireTime > 0) {
                log.debug("OAuth2 token验证成功，用户已登录: userId={}, 过期时间={}秒", userId, expireTime);
            } else {
                log.debug("OAuth2 token验证成功，用户已登录: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("登录Sa-Token失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 从token数据中获取过期时间（秒）
     * 优先使用expiresAt（过期时间点），如果没有则使用accessTokenExpiresAt
     * 
     * @param tokenData token数据
     * @return 过期时间（秒）
     */
    private long getExpireTime(Map<String, Object> tokenData) {
        try {
            // 尝试获取expiresAt字段（格式：yyyy-MM-dd'T'HH:mm:ss）
            Object expiresAt = tokenData.get("expiresAt");
            if (expiresAt != null) {
                String expiresAtStr = expiresAt.toString();
                LocalDateTime expireDateTime = LocalDateTime.parse(expiresAtStr, DATETIME_FORMATTER);
                long seconds = java.time.Duration.between(LocalDateTime.now(), expireDateTime).getSeconds();
                return Math.max(0, seconds); // 如果已经过期，返回0
            }
            
            // 尝试获取accessTokenExpiresAt字段
            Object accessTokenExpiresAt = tokenData.get("accessTokenExpiresAt");
            if (accessTokenExpiresAt != null) {
                String expiresAtStr = accessTokenExpiresAt.toString();
                LocalDateTime expireDateTime = LocalDateTime.parse(expiresAtStr, DATETIME_FORMATTER);
                long seconds = java.time.Duration.between(LocalDateTime.now(), expireDateTime).getSeconds();
                return Math.max(0, seconds);
            }
            
            log.warn("未找到token过期时间字段");
            return 0;
        } catch (Exception e) {
            log.error("解析token过期时间失败: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 检查登录状态
     * 
     * @return 登录状态
     */
    @Operation(summary = "检查登录状态")
    @GetMapping("/check")
    public Result<Map<String, Object>> checkLogin() {
        boolean isLogin = StpUtil.isLogin();
        Map<String, Object> data = new HashMap<>();
        data.put("isLogin", isLogin);
        
        if (isLogin) {
            String userId = StpUtil.getLoginIdAsString();
            String token = StpUtil.getTokenValue();
            data.put("userId", userId);
            data.put("token", token);
        }
        
        return Result.success(data);
    }
    
    /**
     * OAuth2 token信息内部类
     */
    private static class OAuthTokenInfo {
        private final String userId;
        private final long expireTime;
        
        public OAuthTokenInfo(String userId, long expireTime) {
            this.userId = userId;
            this.expireTime = expireTime;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public long getExpireTime() {
            return expireTime;
        }
    }
}

