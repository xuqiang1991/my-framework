package com.myframework.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.api.user.dto.UserPlatformBindRequest;
import com.myframework.common.constant.BusinessConstant;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.ResultCode;
import com.myframework.user.dto.UserRegisterRequest;
import com.myframework.user.entity.User;
import com.myframework.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 用户注册服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterService {
    
    private final UserMapper userMapper;
    private final UserPlatformService userPlatformService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 用户自助注册
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDTO selfRegister(UserRegisterRequest request) {
        // 验证必填字段
        validateRegisterRequest(request);
        
        // TODO: 验证验证码
        // validateCaptcha(request.getCaptcha(), request.getCaptchaKey());
        
        // 检查用户名是否已存在
        if (!isUsernameAvailable(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        
        // 创建用户
        User user = createUser(request);
        
        log.info("用户自助注册成功: username={}, userId={}", user.getUsername(), user.getUserId());
        
        return convertToDTO(user);
    }
    
    /**
     * 管理员创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDTO adminCreateUser(UserRegisterRequest request) {
        // 验证必填字段
        validateRegisterRequest(request);
        
        // 检查用户名是否已存在
        if (!isUsernameAvailable(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        
        // 创建用户
        User user = createUser(request);
        
        log.info("管理员创建用户成功: username={}, userId={}", user.getUsername(), user.getUserId());
        
        return convertToDTO(user);
    }
    
    /**
     * 平台用户注册
     * 用于其他平台通过 SSO 创建用户，并自动绑定平台关系
     * 智能处理策略：
     * 1. 如果该平台用户已绑定账号，直接返回已有用户
     * 2. 如果用户名已被占用，自动生成唯一用户名
     * 3. 如果密码为空，自动生成随机密码
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDTO platformRegister(UserRegisterRequest request, String platformId, String platformUserId) {
        // 验证必填字段
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "用户名不能为空");
        }
        
        if (!StringUtils.hasText(platformId)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "平台ID不能为空");
        }
        
        if (!StringUtils.hasText(platformUserId)) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "平台用户ID不能为空");
        }
        
        // 1. 先检查该平台用户是否已经绑定了账号
        String existingUserId = userPlatformService.findUserIdByPlatformUser(platformId, platformUserId);
        if (StringUtils.hasText(existingUserId)) {
            log.info("平台用户已绑定账号，直接返回: platformId={}, platformUserId={}, userId={}", 
                    platformId, platformUserId, existingUserId);
            
            // 查询已有用户信息并返回
            User existingUser = userMapper.selectById(existingUserId);
            if (existingUser != null) {
                // 更新最后登录时间
                userPlatformService.updateLastLoginTime(existingUserId, platformId);
                return convertToDTO(existingUser);
            }
        }
        
        // 2. 保存原始平台用户名
        String originalUsername = request.getUsername();
        
        // 3. 检查用户名是否已存在，如果存在则生成唯一用户名
        if (!isUsernameAvailable(request.getUsername())) {
            String uniqueUsername = generateUniqueUsername(originalUsername, platformId);
            log.info("平台用户名已存在，生成唯一用户名: original={}, new={}, platformId={}", 
                    originalUsername, uniqueUsername, platformId);
            request.setUsername(uniqueUsername);
        }
        
        // 4. 如果密码为空，生成随机密码（平台用户通过SSO登录，不需要密码）
        if (!StringUtils.hasText(request.getPassword())) {
            String randomPassword = generateRandomPassword();
            request.setPassword(randomPassword);
            log.info("为平台用户生成随机密码: username={}", request.getUsername());
        }
        
        // 5. 创建用户
        User user = createUser(request);
        
        // 6. 自动绑定平台关系（使用原始平台用户名）
        UserPlatformBindRequest bindRequest = new UserPlatformBindRequest();
        bindRequest.setUserId(user.getUserId());
        bindRequest.setPlatformId(platformId);
        bindRequest.setPlatformUserId(platformUserId);
        bindRequest.setPlatformUsername(originalUsername); // 保存原始平台用户名
        
        userPlatformService.bindUserPlatform(bindRequest);
        
        log.info("平台用户注册成功: username={}, originalUsername={}, userId={}, platformId={}", 
                user.getUsername(), originalUsername, user.getUserId(), platformId);
        
        return convertToDTO(user);
    }
    
    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getDeleted, BusinessConstant.Status.NOT_DELETED);
        
        return userMapper.selectCount(wrapper) == 0;
    }
    
    /**
     * 验证注册请求
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "用户名不能为空");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_MISSING.getCode(), "密码不能为空");
        }
        
        // 验证用户名格式
        if (!request.getUsername().matches(BusinessConstant.User.USERNAME_PATTERN)) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), 
                    String.format("用户名格式不正确（%d-%d位字母数字下划线）", 
                            BusinessConstant.User.USERNAME_MIN_LENGTH, 
                            BusinessConstant.User.USERNAME_MAX_LENGTH));
        }
        
        // 验证密码强度
        if (request.getPassword().length() < BusinessConstant.User.PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), 
                    "密码长度至少" + BusinessConstant.User.PASSWORD_MIN_LENGTH + "位");
        }
        
        if (request.getPassword().length() > BusinessConstant.User.PASSWORD_MAX_LENGTH) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR.getCode(), 
                    "密码长度不能超过" + BusinessConstant.User.PASSWORD_MAX_LENGTH + "位");
        }
    }
    
    /**
     * 创建用户
     */
    private User createUser(UserRegisterRequest request) {
        User user = new User();
        BeanUtil.copyProperties(request, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置默认值
        user.setStatus(BusinessConstant.Status.ENABLED);
        user.setDeleted(BusinessConstant.Status.NOT_DELETED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 如果没有昵称，使用用户名
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        
        userMapper.insert(user);
        
        return user;
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(user, dto);
        
        // 不返回密码
        dto.setPassword(null);
        
        // 设置角色
        dto.setRoles(new HashSet<>());
        dto.getRoles().add(BusinessConstant.User.DEFAULT_ROLE);
        
        return dto;
    }
    
    /**
     * 生成唯一用户名
     * 规则：{原用户名}_{平台ID后4位}_{6位随机数字}
     * 如果仍然冲突，继续重试直到找到可用的用户名
     */
    private String generateUniqueUsername(String originalUsername, String platformId) {
        // 取平台ID的后N位作为标识
        String platformSuffix = platformId.length() > BusinessConstant.Platform.PLATFORM_SUFFIX_LENGTH 
                ? platformId.substring(platformId.length() - BusinessConstant.Platform.PLATFORM_SUFFIX_LENGTH) 
                : platformId;
        
        // 生成基础用户名（如果原用户名过长，截取前10位）
        String baseUsername = originalUsername;
        if (baseUsername.length() > 10) {
            baseUsername = baseUsername.substring(0, 10);
        }
        
        // 尝试生成唯一用户名，最多重试指定次数
        for (int i = 0; i < BusinessConstant.Platform.MAX_RETRY_TIMES; i++) {
            // 生成N位随机数字
            int maxRandom = (int) Math.pow(10, BusinessConstant.Platform.RANDOM_NUMBER_LENGTH);
            String randomNum = String.format("%0" + BusinessConstant.Platform.RANDOM_NUMBER_LENGTH + "d", 
                    (int)(Math.random() * maxRandom));
            String newUsername = baseUsername + "_" + platformSuffix + "_" + randomNum;
            
            // 检查是否可用
            if (isUsernameAvailable(newUsername)) {
                return newUsername;
            }
        }
        
        // 如果多次重试都失败，使用UUID确保唯一性
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return baseUsername + "_" + platformSuffix + "_" + uuid;
    }
    
    /**
     * 生成随机密码
     * 生成指定位数的随机密码，包含大小写字母和数字（排除易混淆字符）
     */
    private String generateRandomPassword() {
        // 排除易混淆的字符：0、O、o、1、I、l等
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < BusinessConstant.Platform.RANDOM_PASSWORD_LENGTH; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}

