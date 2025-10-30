package com.myframework.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.api.auth.dto.PlatformDTO;
import com.myframework.api.user.dto.UserPlatformBindRequest;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.ResultCode;
import com.myframework.user.entity.UserPlatform;
import com.myframework.user.mapper.UserPlatformMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户平台关联服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlatformService {
    
    private final UserPlatformMapper userPlatformMapper;
    
    /**
     * 绑定用户平台账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindUserPlatform(UserPlatformBindRequest request) {
        String userId = request.getUserId();
        String platformId = request.getPlatformId();
        
        // 检查是否已经绑定
        LambdaQueryWrapper<UserPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatform::getUserId, userId)
                .eq(UserPlatform::getPlatformId, platformId);
        UserPlatform existingBinding = userPlatformMapper.selectOne(wrapper);
        
        if (existingBinding != null) {
            if (existingBinding.getStatus() == 1) {
                throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "该平台账号已绑定");
            }
            // 如果之前解绑过，重新绑定
            existingBinding.setPlatformUserId(request.getPlatformUserId());
            existingBinding.setPlatformUsername(request.getPlatformUsername());
            existingBinding.setBindTime(LocalDateTime.now());
            existingBinding.setStatus(1);
            userPlatformMapper.updateById(existingBinding);
        } else {
            // 创建新的绑定关系
            UserPlatform userPlatform = new UserPlatform();
            userPlatform.setUserId(userId);
            userPlatform.setPlatformId(platformId);
            userPlatform.setPlatformUserId(request.getPlatformUserId());
            userPlatform.setPlatformUsername(request.getPlatformUsername());
            userPlatform.setBindTime(LocalDateTime.now());
            userPlatform.setStatus(1);
            userPlatformMapper.insert(userPlatform);
        }
        
        log.info("绑定用户平台账号成功: userId={}, platformId={}", userId, platformId);
    }
    
    /**
     * 解绑用户平台账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindUserPlatform(String userId, String platformId) {
        // 查询绑定关系
        LambdaQueryWrapper<UserPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatform::getUserId, userId)
                .eq(UserPlatform::getPlatformId, platformId)
                .eq(UserPlatform::getStatus, 1);
        UserPlatform userPlatform = userPlatformMapper.selectOne(wrapper);
        
        if (userPlatform == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "未找到绑定关系");
        }
        
        // 解绑
        userPlatform.setStatus(0);
        userPlatformMapper.updateById(userPlatform);
        
        log.info("解绑用户平台账号成功: userId={}, platformId={}", userId, platformId);
    }
    
    /**
     * 获取用户已绑定的平台ID列表
     */
    public List<String> getUserBoundPlatformIds(String userId) {
        LambdaQueryWrapper<UserPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatform::getUserId, userId)
                .eq(UserPlatform::getStatus, 1);
        List<UserPlatform> userPlatforms = userPlatformMapper.selectList(wrapper);
        
        return userPlatforms.stream()
                .map(UserPlatform::getPlatformId)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据平台用户ID查找绑定的用户
     */
    public String findUserIdByPlatformUser(String platformId, String platformUserId) {
        LambdaQueryWrapper<UserPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatform::getPlatformId, platformId)
                .eq(UserPlatform::getPlatformUserId, platformUserId)
                .eq(UserPlatform::getStatus, 1);
        UserPlatform userPlatform = userPlatformMapper.selectOne(wrapper);
        
        return userPlatform != null ? userPlatform.getUserId() : null;
    }
    
    /**
     * 更新最后登录时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(String userId, String platformId) {
        LambdaQueryWrapper<UserPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatform::getUserId, userId)
                .eq(UserPlatform::getPlatformId, platformId);
        UserPlatform userPlatform = userPlatformMapper.selectOne(wrapper);
        
        if (userPlatform != null) {
            userPlatform.setLastLoginTime(LocalDateTime.now());
            userPlatformMapper.updateById(userPlatform);
        }
    }
}

