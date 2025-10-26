package com.myframework.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.ResultCode;
import com.myframework.user.entity.User;
import com.myframework.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    /**
     * 根据用户名获取用户
     */
    public UserDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getDeleted, 0);
        
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return null;
        }
        
        return convertToDTO(user);
    }
    
    /**
     * 根据用户ID获取用户
     */
    public UserDTO getUserById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            return null;
        }
        
        return convertToDTO(user);
    }
    
    /**
     * 创建用户
     */
    public UserDTO createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername())
               .eq(User::getDeleted, 0);
        
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        
        // 创建用户
        User user = new User();
        BeanUtil.copyProperties(userDTO, user);
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        return convertToDTO(user);
    }
    
    /**
     * 更新用户
     */
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getUserId());
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        BeanUtil.copyProperties(userDTO, user, "userId", "username", "password", "createTime", "createBy");
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        return convertToDTO(user);
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    public void deleteUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(user, dto);
        
        // 设置角色（示例，实际应该从数据库查询）
        dto.setRoles(new HashSet<>());
        dto.getRoles().add("USER");
        
        return dto;
    }
}

