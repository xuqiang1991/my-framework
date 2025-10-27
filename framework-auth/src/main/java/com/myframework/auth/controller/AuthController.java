package com.myframework.auth.controller;

import com.myframework.api.auth.dto.LoginRequest;
import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.auth.service.AuthService;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、登出、Token管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam(name = "token", required = false) String token) {
        authService.logout(token);
        return Result.success();
    }
    
    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestParam(name = "token") String token) {
        String newToken = authService.refreshToken(token);
        return Result.success(newToken);
    }
    
    /**
     * 验证Token
     */
    @Operation(summary = "验证Token")
    @PostMapping("/validate")
    public Result<Boolean> validateToken(@RequestParam(name = "token") String token) {
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }
    
    /**
     * 获取验证码
     */
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<String> getCaptcha() {
        String captcha = authService.generateCaptcha();
        return Result.success(captcha);
    }
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginResponse> getUserInfo() {
        LoginResponse userInfo = authService.getCurrentUserInfo();
        return Result.success(userInfo);
    }
}

