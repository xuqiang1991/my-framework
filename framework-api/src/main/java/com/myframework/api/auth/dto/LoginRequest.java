package com.myframework.api.auth.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 验证码
     */
    private String captcha;
    
    /**
     * 验证码Key
     */
    private String captchaKey;
    
    /**
     * 记住我
     */
    private Boolean rememberMe;
}

