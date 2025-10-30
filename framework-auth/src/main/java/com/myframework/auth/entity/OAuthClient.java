package com.myframework.auth.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth2客户端实体类
 */
@Data
@TableName("sys_oauth_client")
public class OAuthClient implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客户端ID
     */
    @TableId
    private String clientId;
    
    /**
     * 客户端密钥
     */
    private String clientSecret;
    
    /**
     * 客户端名称
     */
    private String clientName;
    
    /**
     * 关联的平台ID
     */
    private String platformId;
    
    /**
     * 重定向URI列表（逗号分隔）
     */
    private String redirectUris;
    
    /**
     * 授权类型（逗号分隔）
     */
    private String grantTypes;
    
    /**
     * 授权范围
     */
    private String scope;
    
    /**
     * 访问令牌有效期（秒）
     */
    private Integer accessTokenValidity;
    
    /**
     * 刷新令牌有效期（秒）
     */
    private Integer refreshTokenValidity;
    
    /**
     * 自动授权：0-需要用户确认，1-自动授权
     */
    private Integer autoApprove;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

