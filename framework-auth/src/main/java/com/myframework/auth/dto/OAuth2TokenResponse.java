package com.myframework.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * OAuth2令牌响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2TokenResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 访问令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    /**
     * 令牌类型
     */
    @JsonProperty("token_type")
    private String tokenType;
    
    /**
     * 有效期（秒）
     */
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    /**
     * 授权范围
     */
    private String scope;
}

