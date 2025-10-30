package com.myframework.auth.controller;

import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.auth.service.AuthService;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 注意：传统的用户名密码登录已废弃，请使用 OAuth2 SSO 登录
 * 登录入口：/auth/sso/login 或 /oauth2/authorize
 */
@Tag(name = "认证管理", description = "OAuth2 SSO 认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 获取当前用户信息
     * 用于已登录用户获取自己的信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginResponse> getUserInfo() {
        LoginResponse userInfo = authService.getCurrentUserInfo();
        return Result.success(userInfo);
    }
    
    /**
     * 用户登出
     * 清除当前用户的登录状态
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam(name = "token", required = false) String token) {
        authService.logout(token);
        return Result.success();
    }
    
    /**
     * 获取验证码
     * 用于登录页面的验证码功能
     */
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<String> getCaptcha() {
        String captcha = authService.generateCaptcha();
        return Result.success(captcha);
    }
}

