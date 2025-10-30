package com.myframework.api.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户平台绑定请求DTO
 */
@Data
public class UserPlatformBindRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
}

