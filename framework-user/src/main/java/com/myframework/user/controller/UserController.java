package com.myframework.user.controller;

import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import com.myframework.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
     */
    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public Result<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.updateUser(userDTO);
        return Result.success(user);
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.success();
    }
}

