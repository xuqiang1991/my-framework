package com.myframework.api.user;

import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务API接口
 */
@FeignClient(name = "framework-user", contextId = "userApi")
public interface UserApi {
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/user/getByUsername/{username}")
    Result<UserDTO> getUserByUsername(@PathVariable("username") String username);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/getById/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") String userId);
    
    /**
     * 创建用户
     * @param userDTO 用户信息
     * @return 结果
     */
    @PostMapping("/user/create")
    Result<UserDTO> createUser(@RequestBody UserDTO userDTO);
}

