package com.myframework.api.user;

import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;

/**
 * 用户服务RPC接口（Dubbo）
 */
public interface UserApi {

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    Result<UserDTO> getUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<UserDTO> getUserById(String userId);

    /**
     * 创建用户
     * @param userDTO 用户信息
     * @return 结果
     */
    Result<UserDTO> createUser(UserDTO userDTO);
}

 