package com.myframework.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求DTO
 */
@Data
public class UserRegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名（必填）
     */
    private String username;
    
    /**
     * 密码（必填）
     */
    private String password;
    
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
     * 验证码（自助注册时需要）
     */
    private String captcha;
    
    /**
     * 验证码Key
     */
    private String captchaKey;
}

