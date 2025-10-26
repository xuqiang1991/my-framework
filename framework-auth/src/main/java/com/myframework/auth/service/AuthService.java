package com.myframework.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.myframework.api.auth.dto.LoginRequest;
import com.myframework.api.auth.dto.LoginResponse;
import com.myframework.api.user.UserApi;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.constant.CommonConstant;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.Result;
import com.myframework.common.result.ResultCode;
import com.myframework.common.util.JwtUtil;
import com.myframework.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserApi userApi;
    private final RedisUtil redisUtil;
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 参数校验
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_MISSING);
        }
        
        // 2. 验证码校验（实际项目中应该验证）
        // validateCaptcha(request.getCaptcha(), request.getCaptchaKey());
        
        // 3. 查询用户信息
        Result<UserDTO> userResult = userApi.getUserByUsername(request.getUsername());
        if (userResult.getData() == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserDTO user = userResult.getData();
        
        // 4. 验证密码（实际项目中应该使用加密密码）
        if (!request.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 5. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 6. 使用Sa-Token进行登录
        StpUtil.login(user.getUserId());
        
        // 7. 生成JWT Token
        String jwtToken = JwtUtil.generateToken(user.getUserId(), user.getUsername());
        
        // 8. 获取Sa-Token的tokenValue
        String saToken = StpUtil.getTokenValue();
        
        // 9. 将用户信息缓存到Redis
        String cacheKey = CommonConstant.LOGIN_TOKEN_KEY + saToken;
        redisUtil.set(cacheKey, user, CommonConstant.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 10. 构建响应
        return LoginResponse.builder()
                .accessToken(saToken)
                .refreshToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(CommonConstant.DEFAULT_EXPIRE_TIME)
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(user.getRoles() != null ? user.getRoles() : new HashSet<>())
                .permissions(new HashSet<>())
                .build();
    }
    
    /**
     * 用户登出
     */
    public void logout(String token) {
        try {
            // 使用Sa-Token登出
            if (StringUtils.hasText(token)) {
                StpUtil.logoutByTokenValue(token);
            } else {
                StpUtil.logout();
            }
            
            // 清除Redis缓存
            String cacheKey = CommonConstant.LOGIN_TOKEN_KEY + (StringUtils.hasText(token) ? token : StpUtil.getTokenValue());
            redisUtil.delete(cacheKey);
            
            log.info("用户登出成功");
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        // 验证JWT Token
        if (!JwtUtil.validateToken(token)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }
        
        // 刷新JWT Token
        String newToken = JwtUtil.refreshToken(token);
        
        if (newToken == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        
        return newToken;
    }
    
    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        
        try {
            // 使用Sa-Token验证
            Object loginId = StpUtil.getLoginIdByToken(token);
            return loginId != null;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 生成验证码
     */
    public String generateCaptcha() {
        // 生成验证码Key
        String captchaKey = IdUtil.simpleUUID();
        
        // 生成4位数字验证码
        String captchaCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        
        // 缓存验证码
        String cacheKey = CommonConstant.CAPTCHA_CODE_KEY + captchaKey;
        redisUtil.set(cacheKey, captchaCode, CommonConstant.CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);
        
        log.info("生成验证码: key={}, code={}", captchaKey, captchaCode);
        
        return captchaKey;
    }
    
    /**
     * 验证验证码
     */
    private void validateCaptcha(String captcha, String captchaKey) {
        if (!StringUtils.hasText(captcha) || !StringUtils.hasText(captchaKey)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "验证码不能为空");
        }
        
        String cacheKey = CommonConstant.CAPTCHA_CODE_KEY + captchaKey;
        Object cachedCode = redisUtil.get(cacheKey);
        
        if (cachedCode == null) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "验证码已过期");
        }
        
        if (!captcha.equals(cachedCode.toString())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), "验证码错误");
        }
        
        // 验证成功后删除验证码
        redisUtil.delete(cacheKey);
    }
    
    /**
     * 获取当前登录用户信息
     */
    public LoginResponse getCurrentUserInfo() {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 从缓存获取用户信息
        String cacheKey = CommonConstant.LOGIN_TOKEN_KEY + StpUtil.getTokenValue();
        Object cachedUser = redisUtil.get(cacheKey);
        
        if (cachedUser instanceof UserDTO user) {
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .roles(user.getRoles() != null ? user.getRoles() : new HashSet<>())
                    .permissions(new HashSet<>())
                    .build();
        }
        
        // 缓存中没有，从用户服务获取
        Result<UserDTO> userResult = userApi.getUserById(userId);
        if (userResult.getData() == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserDTO user = userResult.getData();
        
        return LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(user.getRoles() != null ? user.getRoles() : new HashSet<>())
                .permissions(new HashSet<>())
                .build();
    }
}

