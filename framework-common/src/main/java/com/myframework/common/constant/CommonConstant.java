package com.myframework.common.constant;

/**
 * 公共常量类
 */
public class CommonConstant {
    
    /**
     * UTF-8 编码
     */
    public static final String UTF8 = "UTF-8";
    
    /**
     * GBK 编码
     */
    public static final String GBK = "GBK";
    
    /**
     * Token请求头
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 登录用户Key
     */
    public static final String LOGIN_USER_KEY = "login_user";
    
    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";
    
    /**
     * 用户名
     */
    public static final String USER_NAME = "userName";
    
    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;
    
    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;
    
    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_CODE_KEY = "captcha:code:";
    
    /**
     * 登录用户Token缓存前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login:token:";
    
    /**
     * 默认过期时间（秒） - 7天
     */
    public static final long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60;
    
    /**
     * 验证码过期时间（秒） - 5分钟
     */
    public static final long CAPTCHA_EXPIRE_TIME = 5 * 60;
}

