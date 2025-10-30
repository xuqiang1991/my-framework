package com.myframework.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.myframework.api.auth.dto.LoginRequest;
import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.auth.dto.OAuth2AuthorizeRequest;
import com.myframework.auth.service.AuthService;
import com.myframework.auth.service.OAuth2Service;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SSO统一登录控制器
 */
@Slf4j
@Tag(name = "SSO统一登录", description = "单点登录统一认证接口")
@RestController
@RequestMapping("/auth/sso")
@RequiredArgsConstructor
public class SSOController {
    
    private final AuthService authService;
    private final OAuth2Service oauth2Service;
    
    /**
     * SSO登录页面信息
     * 返回登录页面需要的客户端信息
     */
    @Operation(summary = "获取SSO登录页面信息")
    @GetMapping("/login")
    public Result<Map<String, Object>> getLoginInfo(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state) {
        
        log.info("获取SSO登录页面信息: clientId={}", clientId);
        
        // 验证客户端
        var client = oauth2Service.validateClient(clientId, null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("clientId", clientId);
        result.put("clientName", client.getClientName());
        result.put("redirectUri", redirectUri);
        result.put("scope", scope);
        result.put("state", state);
        result.put("isLogin", StpUtil.isLogin());
        
        return Result.success(result);
    }
    
    /**
     * SSO登录
     * 用户在SSO登录页面提交登录信息
     */
    @Operation(summary = "SSO登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> ssoLogin(
            @RequestBody LoginRequest loginRequest,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state) {
        
        log.info("SSO登录: username={}, clientId={}", loginRequest.getUsername(), clientId);
        
        // 执行登录
        LoginResponse loginResponse = authService.login(loginRequest);
        
        // 生成授权码
        OAuth2AuthorizeRequest authorizeRequest = new OAuth2AuthorizeRequest();
        authorizeRequest.setClientId(clientId);
        authorizeRequest.setRedirectUri(redirectUri);
        authorizeRequest.setScope(scope);
        authorizeRequest.setState(state);
        
        String code = oauth2Service.generateAuthorizationCode(loginResponse.getUserId(), authorizeRequest);
        
        // 构建重定向URL
        String redirectUrl = redirectUri + 
                "?code=" + code +
                (state != null ? "&state=" + state : "");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("redirectUrl", redirectUrl);
        result.put("userInfo", loginResponse);
        
        return Result.success(result);
    }
    
    /**
     * SSO授权确认页面信息
     * 返回授权确认页面需要的信息
     */
    @Operation(summary = "获取SSO授权确认页面信息")
    @GetMapping("/approve")
    public Result<Map<String, Object>> getApproveInfo(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state) {
        
        log.info("获取SSO授权确认页面信息: clientId={}", clientId);
        
        // 验证客户端
        var client = oauth2Service.validateClient(clientId, null);
        
        // 获取当前用户信息
        LoginResponse userInfo = authService.getCurrentUserInfo();
        
        Map<String, Object> result = new HashMap<>();
        result.put("clientId", clientId);
        result.put("clientName", client.getClientName());
        result.put("redirectUri", redirectUri);
        result.put("scope", scope);
        result.put("state", state);
        result.put("userInfo", userInfo);
        
        return Result.success(result);
    }
    
    /**
     * 检查登录状态
     */
    @Operation(summary = "检查登录状态")
    @GetMapping("/check")
    public Result<Map<String, Object>> checkLogin() {
        Map<String, Object> result = new HashMap<>();
        result.put("isLogin", StpUtil.isLogin());
        
        if (StpUtil.isLogin()) {
            LoginResponse userInfo = authService.getCurrentUserInfo();
            result.put("userInfo", userInfo);
        }
        
        return Result.success(result);
    }
    
    /**
     * SSO登出
     * 从所有平台登出
     */
    @Operation(summary = "SSO登出")
    @PostMapping("/logout")
    public Result<Void> ssoLogout() {
        log.info("SSO登出");
        
        // 执行登出
        authService.logout(null);
        
        return Result.success();
    }
}

