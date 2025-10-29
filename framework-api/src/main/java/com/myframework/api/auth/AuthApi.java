package com.myframework.api.auth;

import com.myframework.api.auth.dto.LoginRequest;
import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.common.result.Result;

/**
 * 认证服务RPC接口（Dubbo）
 */
public interface AuthApi {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    Result<LoginResponse> login(LoginRequest request);

    /**
     * 用户登出
     * @param token Token
     * @return 结果
     */
    Result<Void> logout(String token);

    /**
     * 刷新Token
     * @param token 原Token
     * @return 新Token
     */
    Result<String> refreshToken(String token);

    /**
     * 验证Token
     * @param token Token
     * @return 是否有效
     */
    Result<Boolean> validateToken(String token);
}

