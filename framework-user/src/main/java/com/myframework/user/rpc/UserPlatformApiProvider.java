package com.myframework.user.rpc;

import com.myframework.api.auth.PlatformApi;
import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.api.user.UserPlatformApi;
import com.myframework.api.user.dto.UserPlatformBindRequest;
import com.myframework.common.result.Result;
import com.myframework.user.service.UserPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 用户平台关联API RPC提供者
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class UserPlatformApiProvider implements UserPlatformApi {
    
    private final UserPlatformService userPlatformService;
    
    @DubboReference(check = false)
    private PlatformApi platformApi;
    
    @Override
    public Result<Void> bindUserPlatform(UserPlatformBindRequest request) {
        try {
            userPlatformService.bindUserPlatform(request);
            return Result.success();
        } catch (Exception e) {
            log.error("绑定用户平台账号失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> unbindUserPlatform(String userId, String platformId) {
        try {
            userPlatformService.unbindUserPlatform(userId, platformId);
            return Result.success();
        } catch (Exception e) {
            log.error("解绑用户平台账号失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<List<PlatformDTO>> getUserBoundPlatforms(String userId) {
        try {
            // 获取用户绑定的平台ID列表
            List<String> platformIds = userPlatformService.getUserBoundPlatformIds(userId);
            
            // 通过Dubbo调用auth服务获取平台详情
            Result<List<PlatformDTO>> platformsResult = platformApi.getPlatformsByIds(platformIds);
            
            return platformsResult;
        } catch (Exception e) {
            log.error("获取用户已绑定平台列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<String> findUserIdByPlatformUser(String platformId, String platformUserId) {
        try {
            String userId = userPlatformService.findUserIdByPlatformUser(platformId, platformUserId);
            return Result.success(userId);
        } catch (Exception e) {
            log.error("根据平台用户查找用户失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    @Override
    public Result<Void> updateLastLoginTime(String userId, String platformId) {
        try {
            userPlatformService.updateLastLoginTime(userId, platformId);
            return Result.success();
        } catch (Exception e) {
            log.error("更新最后登录时间失败", e);
            return Result.error(e.getMessage());
        }
    }
}

