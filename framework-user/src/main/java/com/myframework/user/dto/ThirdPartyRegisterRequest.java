package com.myframework.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方平台用户注册请求DTO
 */
@Data
public class ThirdPartyRegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 第三方平台ID（由系统分配）
     */
    private String platformId;
    
    /**
     * 第三方平台的用户ID
     */
    private String externalUserId;
    
    /**
     * 第三方平台的用户名
     */
    private String externalUsername;
    
    /**
     * 用户名（可选，如不提供则自动生成）
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * API Key（用于身份验证）
     */
    private String apiKey;
    
    /**
     * API Secret（用于签名验证）
     */
    private String apiSecret;
    
    /**
     * 时间戳（用于防重放攻击）
     */
    private Long timestamp;
    
    /**
     * 签名（用于验证请求完整性）
     */
    private String signature;
}

