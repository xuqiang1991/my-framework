package com.myframework.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myframework.api.user.dto.UserDTO;
import com.myframework.common.exception.BusinessException;
import com.myframework.common.result.ResultCode;
import com.myframework.user.dto.UserStatsDTO;
import com.myframework.user.entity.User;
import com.myframework.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    /**
     * 查询用户列表（支持搜索和分页）
     */
    public List<UserDTO> getUserList(Integer page, Integer size, String keyword, String platformId) {
        Page<User> userPage = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        
        // 如果指定了平台ID，则按平台筛选
        if (StrUtil.isNotBlank(platformId)) {
            wrapper.eq(User::getPlatformId, platformId);
        }
        
        // 如果有关键词，则进行模糊搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                             .or().like(User::getNickname, keyword)
                             .or().like(User::getEmail, keyword)
                             .or().like(User::getPhone, keyword));
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(userPage, wrapper);
        
        return result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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
     * 根据用户ID获取用户实体（内部使用）
     */
    public User getById(String userId) {
        return userMapper.selectById(userId);
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
        
        // 使用 MyBatis Plus 的逻辑删除方法
        // 配置了逻辑删除后，deleteById 会自动将 deleted 字段设置为 1
        int result = userMapper.deleteById(userId);
        
        if (result > 0) {
            log.info("用户删除成功: userId={}, username={}", userId, user.getUsername());
        } else {
            log.error("用户删除失败: userId={}", userId);
            throw new BusinessException(ResultCode.ERROR.getCode(), "删除用户失败");
        }
    }
    
    /**
     * 获取用户统计数据
     */
    public UserStatsDTO getUserStats(String platformId) {
        UserStatsDTO stats = new UserStatsDTO();
        
        // 总用户数（未删除）
        LambdaQueryWrapper<User> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            totalWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setTotalUsers(userMapper.selectCount(totalWrapper));
        
        // 活跃用户数（启用且未删除）
        LambdaQueryWrapper<User> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(User::getStatus, 1)
                    .eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            activeWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setActiveUsers(userMapper.selectCount(activeWrapper));
        
        // 禁用用户数
        LambdaQueryWrapper<User> disabledWrapper = new LambdaQueryWrapper<>();
        disabledWrapper.eq(User::getStatus, 0)
                      .eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            disabledWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setDisabledUsers(userMapper.selectCount(disabledWrapper));
        
        // 今日注册用户数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<User> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.between(User::getCreateTime, todayStart, todayEnd)
                   .eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            todayWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setTodayRegistered(userMapper.selectCount(todayWrapper));
        
        // 本周注册用户数
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LambdaQueryWrapper<User> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.ge(User::getCreateTime, weekStart)
                  .eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            weekWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setWeekRegistered(userMapper.selectCount(weekWrapper));
        
        // 本月注册用户数
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LambdaQueryWrapper<User> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.ge(User::getCreateTime, monthStart)
                   .eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(platformId)) {
            monthWrapper.eq(User::getPlatformId, platformId);
        }
        stats.setMonthRegistered(userMapper.selectCount(monthWrapper));
        
        String platformInfo = StrUtil.isNotBlank(platformId) ? "platform=" + platformId : "all platforms";
        log.info("用户统计数据({}): total={}, active={}, todayRegistered={}", 
                platformInfo, stats.getTotalUsers(), stats.getActiveUsers(), stats.getTodayRegistered());
        
        return stats;
    }
    
    /**
     * 转换为DTO
     */
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(user, dto);
        
        // 设置角色（示例，实际应该从数据库查询）
        dto.setRoles(new HashSet<>());
        dto.getRoles().add("USER");
        
        return dto;
    }
}

