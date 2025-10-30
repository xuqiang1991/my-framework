package com.myframework.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("grant_type")
    private String grantType;
    
    /**
     * 授权码（当grantType为authorization_code时必填）
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * 刷新令牌（当grantType为refresh_token时必填）
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    /**
     * 客户端ID
     */
    @JsonProperty("client_id")
    private String clientId;
    
    /**
     * 客户端密钥
     */
    @JsonProperty("client_secret")
    private String clientSecret;
    
    /**
     * 重定向URI（当grantType为authorization_code时必填）
     */
    @JsonProperty("redirect_uri")
    private String redirectUri;
}

