package com.myframework.api.user;

import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.api.user.dto.UserPlatformBindRequest;
import com.myframework.common.result.Result;

import java.util.List;

/**
 * 用户平台关联API接口
 */
public interface UserPlatformApi {
    
    /**
     * 绑定用户平台账号
     * @param request 绑定请求
     * @return 操作结果
     */
    Result<Void> bindUserPlatform(UserPlatformBindRequest request);
    
    /**
     * 解绑用户平台账号
     * @param userId 用户ID
     * @param platformId 平台ID
     * @return 操作结果
     */
    Result<Void> unbindUserPlatform(String userId, String platformId);
    
    /**
     * 获取用户已绑定的平台列表
     * @param userId 用户ID
     * @return 平台列表
     */
    Result<List<PlatformDTO>> getUserBoundPlatforms(String userId);
    
    /**
     * 根据平台用户ID查找绑定的用户
     * @param platformId 平台ID
     * @param platformUserId 平台用户ID
     * @return 用户ID
     */
    Result<String> findUserIdByPlatformUser(String platformId, String platformUserId);
    
    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @param platformId 平台ID
     * @return 操作结果
     */
    Result<Void> updateLastLoginTime(String userId, String platformId);
}

