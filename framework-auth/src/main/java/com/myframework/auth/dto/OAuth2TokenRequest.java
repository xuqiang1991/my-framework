package com.myframework.auth.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * OAuth2令牌请求DTO
 */
@Data
public class OAuth2TokenRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 授权类型（authorization_code或refresh_token）
     */
    private String grantType;
    
    /**
     * 授权码（当grantType为authorization_code时必填）
     */
    private String code;
    
    /**
     * 刷新令牌（当grantType为refresh_token时必填）
     */
    private String refreshToken;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
    /**
     * 客户端密钥
     */
    private String clientSecret;
    
    /**
     * 重定向URI（当grantType为authorization_code时必填）
     */
    private String redirectUri;
}

