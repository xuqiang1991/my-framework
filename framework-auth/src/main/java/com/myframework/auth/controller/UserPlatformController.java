package com.myframework.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.myframework.api.auth.PlatformApi;
import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.api.user.UserPlatformApi;
import com.myframework.api.user.dto.UserPlatformBindRequest;
import com.myframework.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户平台关联控制器
 */
@Tag(name = "用户平台关联", description = "用户多平台账号绑定管理")
@RestController
@RequestMapping("/auth/platform")
@RequiredArgsConstructor
public class UserPlatformController {
    
    @DubboReference(check = false)
    private UserPlatformApi userPlatformApi;
    
    @DubboReference(check = false)
    private PlatformApi platformApi;
    
    /**
     * 绑定平台账号
     */
    @Operation(summary = "绑定平台账号")
    @PostMapping("/bind")
    public Result<Void> bindPlatform(@RequestBody UserPlatformBindRequest request) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        request.setUserId(userId);
        
        return userPlatformApi.bindUserPlatform(request);
    }
    
    /**
     * 解绑平台账号
     */
    @Operation(summary = "解绑平台账号")
    @PostMapping("/unbind")
    public Result<Void> unbindPlatform(@RequestParam("platformId") String platformId) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        return userPlatformApi.unbindUserPlatform(userId, platformId);
    }
    
    /**
     * 获取用户已绑定的平台列表
     */
    @Operation(summary = "获取用户已绑定的平台列表")
    @GetMapping("/bound")
    public Result<List<PlatformDTO>> getBoundPlatforms() {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        return userPlatformApi.getUserBoundPlatforms(userId);
    }
    
    /**
     * 获取所有可用平台列表
     */
    @Operation(summary = "获取所有可用平台列表")
    @GetMapping("/list")
    public Result<List<PlatformDTO>> getAllPlatforms() {
        return platformApi.getAllPlatforms();
    }
}

