package com.myframework.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户统计数据DTO
 */
@Data
public class UserStatsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 总用户数
     */
    private Long totalUsers;
    
    /**
     * 活跃用户数（状态为启用的用户）
     */
    private Long activeUsers;
    
    /**
     * 今日注册用户数
     */
    private Long todayRegistered;
    
    /**
     * 本周注册用户数
     */
    private Long weekRegistered;
    
    /**
     * 本月注册用户数
     */
    private Long monthRegistered;
    
    /**
     * 禁用用户数
     */
    private Long disabledUsers;
}

