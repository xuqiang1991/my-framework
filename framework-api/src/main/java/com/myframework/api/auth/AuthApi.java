package com.myframework.api.auth;

import com.myframework.api.auth.dto.LoginRequest;
import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证服务API接口
 */
@FeignClient(name = "framework-auth", contextId = "authApi")
public interface AuthApi {
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/auth/login")
    Result<LoginResponse> login(@RequestBody LoginRequest request);
    
    /**
     * 用户登出
     * @param token Token
     * @return 结果
     */
    @PostMapping("/auth/logout")
    Result<Void> logout(@RequestParam("token") String token);
    
    /**
     * 刷新Token
     * @param token 原Token
     * @return 新Token
     */
    @PostMapping("/auth/refresh")
    Result<String> refreshToken(@RequestParam("token") String token);
    
    /**
     * 验证Token
     * @param token Token
     * @return 是否有效
     */
    @PostMapping("/auth/validate")
    Result<Boolean> validateToken(@RequestParam("token") String token);
}

