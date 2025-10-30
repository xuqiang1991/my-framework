package com.myframework.auth.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * OAuth2授权请求DTO
 */
@Data
public class OAuth2AuthorizeRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应类型（固定为code）
     */
    private String responseType;
    
    /**
     * 客户端ID
     */
    private String clientId;
    
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
}

