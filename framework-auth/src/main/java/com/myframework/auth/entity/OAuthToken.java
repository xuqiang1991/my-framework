package com.myframework.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth2令牌实体类
 */
@Data
@TableName("sys_oauth_token")
public class OAuthToken implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 授权范围
     */
    private String scope;
    
    /**
     * 令牌类型
     */
    private String tokenType;
    
    /**
     * 访问令牌过期时间
     */
    private LocalDateTime accessTokenExpiresAt;
    
    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshTokenExpiresAt;
    
    /**
     * 是否已撤销：0-有效，1-已撤销
     */
    private Integer revoked;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

