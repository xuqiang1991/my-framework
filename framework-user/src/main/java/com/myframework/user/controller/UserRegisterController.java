package com.myframework.user.controller;

import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import com.myframework.user.dto.UserRegisterRequest;
import com.myframework.user.service.UserRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户注册控制器
 */
@Tag(name = "用户注册", description = "用户注册和账号创建")
@RestController
@RequestMapping("/user/register")
@RequiredArgsConstructor
public class UserRegisterController {
    
    private final UserRegisterService userRegisterService;
    
    /**
     * 用户自助注册
     * 用于新用户注册账号
     */
    @Operation(summary = "用户自助注册")
    @PostMapping("/self")
    public Result<UserDTO> selfRegister(@RequestBody UserRegisterRequest request) {
        UserDTO user = userRegisterService.selfRegister(request);
        return Result.success(user);
    }
    
    /**
     * 管理员创建用户
     * 需要管理员权限
     */
    @Operation(summary = "管理员创建用户")
    @PostMapping("/admin")
    public Result<UserDTO> adminCreateUser(@RequestBody UserRegisterRequest request) {
        UserDTO user = userRegisterService.adminCreateUser(request);
        return Result.success(user);
    }
    
    /**
     * 平台用户注册
     * 用于其他平台通过 SSO 创建用户
     * 注意：
     * 1. 如果用户名已被占用，系统会自动生成唯一用户名
     * 2. 密码字段可以为空，系统会自动生成随机密码（平台用户通过SSO登录，不需要密码）
     * 3. 原始平台用户名会保存在用户平台关联表中
     */
    @Operation(summary = "平台用户注册", 
               description = "为SSO平台创建用户账号。如果用户名冲突，系统会自动生成唯一用户名。")
    @PostMapping("/platform")
    public Result<UserDTO> platformRegister(
            @RequestBody UserRegisterRequest request,
            @RequestParam("platformId") String platformId,
            @RequestParam("platformUserId") String platformUserId) {
        UserDTO user = userRegisterService.platformRegister(request, platformId, platformUserId);
        return Result.success(user);
    }
    
    /**
     * 检查用户名是否可用
     */
    @Operation(summary = "检查用户名是否可用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam("username") String username) {
        boolean available = userRegisterService.isUsernameAvailable(username);
        return Result.success(available);
    }
}

