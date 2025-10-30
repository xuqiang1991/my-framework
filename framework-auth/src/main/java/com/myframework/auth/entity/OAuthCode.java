package com.myframework.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth2授权码实体类
 */
@Data
@TableName("sys_oauth_code")
public class OAuthCode implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 授权码
     */
    private String code;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 重定向URI
     */
    private String redirectUri;
    
    /**
     * 授权范围
     */
    private String scope;
    
    /**
     * 状态码（防CSRF攻击）
     */
    private String state;
    
    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;
    
    /**
     * 是否已使用：0-未使用，1-已使用
     */
    private Integer used;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

