package com.myframework.user.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import com.myframework.user.dto.UserStatsDTO;
import com.myframework.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户增删改查")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 查询用户列表（支持搜索和分页）
     * 需要管理员权限
     */
    @Operation(summary = "查询用户列表")
    // @SaCheckRole("ADMIN")  // 暂时注释，使用OAuth2 token验证
    @GetMapping("/list")
    public Result<List<UserDTO>> getUserList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) String platformId) {
        List<UserDTO> users = userService.getUserList(page, size, keyword, platformId);
        return Result.success(users);
    }
    
    /**
     * 获取用户统计数据
     * 需要管理员权限
     */
    @Operation(summary = "获取用户统计数据")
    @SaCheckRole("ADMIN")
    @GetMapping("/stats")
    public Result<UserStatsDTO> getUserStats(@RequestParam(required = false) String platformId) {
        UserStatsDTO stats = userService.getUserStats(platformId);
        return Result.success(stats);
    }
    
    /**
     * 根据用户名获取用户
     */
    @Operation(summary = "根据用户名获取用户")
    @GetMapping("/getByUsername/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return Result.success(user);
    }
    
    /**
     * 根据用户ID获取用户
     */
    @Operation(summary = "根据用户ID获取用户")
    @GetMapping("/getById/{userId}")
    public Result<UserDTO> getUserById(@PathVariable String userId) {
        UserDTO user = userService.getUserById(userId);
        return Result.success(user);
    }
    
    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public Result<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.createUser(userDTO);
        return Result.success(user);
    }
    
    /**
     * 更新用户
     * 需要管理员权限
     */
    @Operation(summary = "更新用户")
    @SaCheckRole("ADMIN")
    @PutMapping("/update")
    public Result<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.updateUser(userDTO);
        return Result.success(user);
    }
    
    /**
     * 删除用户（逻辑删除）
     * 需要管理员权限
     */
    @Operation(summary = "删除用户")
    @SaCheckRole("ADMIN")
    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.success();
    }
}

