package com.myframework.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.result.ResultCode;
import com.myframework.common.exception.BusinessException;
import com.myframework.user.dto.ThirdPartyRegisterRequest;
import com.myframework.user.entity.ApiCredential;
import com.myframework.user.entity.User;
import com.myframework.user.mapper.ApiCredentialMapper;
import com.myframework.user.mapper.UserPlatformMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.TreeMap;

/**
 * 第三方平台API服务
 * 提供API Key认证、签名验证、用户查询等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyApiService {
    
    private final ApiCredentialMapper apiCredentialMapper;
    private final UserPlatformMapper userPlatformMapper;
    private final UserService userService;
    
    /**
     * 验证第三方API请求
     * 包括：API Key验证、签名验证、时间戳验证（防重放攻击）
     */
    public void validateApiRequest(ThirdPartyRegisterRequest request) {
        // 1. 验证必填字段
        if (!StringUtils.hasText(request.getApiKey())) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "API Key不能为空");
        }
        
        if (!StringUtils.hasText(request.getSignature())) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "签名不能为空");
        }
        
        if (request.getTimestamp() == null) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "时间戳不能为空");
        }
        
        // 2. 验证时间戳（防重放攻击，允许5分钟误差）
        long currentTime = System.currentTimeMillis();
        long timeDiff = Math.abs(currentTime - request.getTimestamp());
        if (timeDiff > 5 * 60 * 1000) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "请求已过期，请检查系统时间");
        }
        
        // 3. 验证API Key
        ApiCredential credential = apiCredentialMapper.selectByApiKey(request.getApiKey());
        if (credential == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "无效的API Key");
        }
        
        if (credential.getStatus() != 1) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "API Key已被禁用");
        }
        
        if (credential.getExpiresAt() != null && credential.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "API Key已过期");
        }
        
        // 4. 验证平台ID
        if (!StringUtils.hasText(request.getPlatformId()) || 
            !request.getPlatformId().equals(credential.getPlatformId())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "平台ID不匹配");
        }
        
        // 5. 验证签名
        String calculatedSignature = calculateSignature(request, credential.getApiSecret());
        if (!calculatedSignature.equals(request.getSignature())) {
            log.warn("签名验证失败: platformId={}, apiKey={}, expected={}, actual={}", 
                    request.getPlatformId(), request.getApiKey(), calculatedSignature, request.getSignature());
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "签名验证失败");
        }
        
        // 6. 更新最后使用时间
        credential.setLastUsedAt(LocalDateTime.now());
        apiCredentialMapper.updateById(credential);
        
        log.info("第三方API请求验证成功: platformId={}, apiKey={}", request.getPlatformId(), request.getApiKey());
    }
    
    /**
     * 验证API Key（简化版本，仅用于查询接口）
     */
    public void validateApiKey(String platformId, String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "API Key不能为空");
        }
        
        ApiCredential credential = apiCredentialMapper.selectByApiKey(apiKey);
        if (credential == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "无效的API Key");
        }
        
        if (credential.getStatus() != 1) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "API Key已被禁用");
        }
        
        if (!platformId.equals(credential.getPlatformId())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "平台ID不匹配");
        }
        
        // 更新最后使用时间
        credential.setLastUsedAt(LocalDateTime.now());
        apiCredentialMapper.updateById(credential);
    }
    
    /**
     * 查询平台绑定的用户
     */
    public UserDTO getBindUser(String platformId, String externalUserId) {
        String userId = userPlatformMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.myframework.user.entity.UserPlatform>()
                .eq(com.myframework.user.entity.UserPlatform::getPlatformId, platformId)
                .eq(com.myframework.user.entity.UserPlatform::getPlatformUserId, externalUserId)
                .eq(com.myframework.user.entity.UserPlatform::getStatus, 1)
        ).getUserId();
        
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return null;
        }
        
        return userService.convertToDTO(user);
    }
    
    /**
     * 生成API凭证
     * 管理员为第三方平台生成API Key和Secret
     */
    public String generateApiKey(String platformId) {
        if (!StringUtils.hasText(platformId)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "平台ID不能为空");
        }
        
        // 生成API Key和Secret
        String apiKey = "ak_" + IdUtil.simpleUUID();
        String apiSecret = "sk_" + IdUtil.simpleUUID();
        
        // 保存到数据库
        ApiCredential credential = new ApiCredential();
        credential.setPlatformId(platformId);
        credential.setApiKey(apiKey);
        credential.setApiSecret(apiSecret);
        credential.setStatus(1);
        credential.setCreatedAt(LocalDateTime.now());
        credential.setUpdatedAt(LocalDateTime.now());
        
        apiCredentialMapper.insert(credential);
        
        log.info("为平台生成API凭证: platformId={}, apiKey={}", platformId, apiKey);
        
        // 返回凭证信息（只返回一次，之后无法再查看Secret）
        return String.format("API Key: %s\nAPI Secret: %s\n\n请妥善保管API Secret，它将不会再次显示！", 
                apiKey, apiSecret);
    }
    
    /**
     * 计算请求签名
     * 签名算法：MD5(apiKey + apiSecret + timestamp + platformId + externalUserId + username)
     * 参数按字母顺序排序后拼接
     */
    private String calculateSignature(ThirdPartyRegisterRequest request, String apiSecret) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("apiKey", request.getApiKey());
        params.put("timestamp", String.valueOf(request.getTimestamp()));
        params.put("platformId", request.getPlatformId());
        
        if (StringUtils.hasText(request.getExternalUserId())) {
            params.put("externalUserId", request.getExternalUserId());
        }
        if (StringUtils.hasText(request.getUsername())) {
            params.put("username", request.getUsername());
        }
        
        // 按字母顺序拼接参数
        StringBuilder signStr = new StringBuilder();
        for (String value : params.values()) {
            signStr.append(value);
        }
        // 最后拼接apiSecret
        signStr.append(apiSecret);
        
        // 计算MD5
        String signature = SecureUtil.md5(signStr.toString());
        
        log.debug("计算签名: signStr={}, signature={}", signStr.toString(), signature);
        
        return signature;
    }
}

