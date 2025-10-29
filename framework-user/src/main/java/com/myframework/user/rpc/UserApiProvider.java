package com.myframework.user.rpc;

import com.myframework.api.user.UserApi;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.Result;
import com.myframework.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 用户服务 Dubbo 提供者实现
 */
@DubboService
@RequiredArgsConstructor
public class UserApiProvider implements UserApi {

    private final UserService userService;

    @Override
    public Result<UserDTO> getUserByUsername(String username) {
        UserDTO user = userService.getUserByUsername(username);
        return Result.success(user);
    }

    @Override
    public Result<UserDTO> getUserById(String userId) {
        UserDTO user = userService.getUserById(userId);
        return Result.success(user);
    }

    @Override
    public Result<UserDTO> createUser(UserDTO userDTO) {
        UserDTO user = userService.createUser(userDTO);
        return Result.success(user);
    }
}


