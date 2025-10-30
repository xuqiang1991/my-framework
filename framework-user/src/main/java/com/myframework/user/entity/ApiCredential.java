package com.myframework.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API凭证实体类
 * 用于管理第三方平台的API Key和Secret
 */
@Data
@TableName("sys_api_credential")
public class ApiCredential implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 凭证ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String credentialId;
    
    /**
     * 平台ID
     */
    private String platformId;
    
    /**
     * API Key（公开）
     */
    private String apiKey;
    
    /**
     * API Secret（私密，需加密存储）
     */
    private String apiSecret;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 过期时间（可选，null表示永不过期）
     */
    private LocalDateTime expiresAt;
    
    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 备注
     */
    private String remark;
}

