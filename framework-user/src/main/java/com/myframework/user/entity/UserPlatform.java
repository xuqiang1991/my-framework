package com.myframework.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户平台关联实体类
 */
@Data
@TableName("sys_user_platform")
public class UserPlatform implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 平台ID
     */
    private String platformId;
    
    /**
     * 平台用户ID
     */
    private String platformUserId;
    
    /**
     * 平台用户名
     */
    private String platformUsername;
    
    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 状态：0-解绑，1-已绑定
     */
    private Integer status;
}

