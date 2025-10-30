package com.myframework.common.constant;

/**
 * 业务常量类
 * 定义系统中使用的业务常量
 */
public class BusinessConstant {
    
    /**
     * 用户相关常量
     */
    public static class User {
        /** 用户名最小长度 */
        public static final int USERNAME_MIN_LENGTH = 4;
        /** 用户名最大长度 */
        public static final int USERNAME_MAX_LENGTH = 20;
        /** 密码最小长度 */
        public static final int PASSWORD_MIN_LENGTH = 6;
        /** 密码最大长度 */
        public static final int PASSWORD_MAX_LENGTH = 32;
        /** 用户名正则表达式 */
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,20}$";
        /** 默认角色 */
        public static final String DEFAULT_ROLE = "USER";
        /** 管理员角色 */
        public static final String ADMIN_ROLE = "ADMIN";
    }
    
    /**
     * OAuth2相关常量
     */
    public static class OAuth2 {
        /** 授权码有效期（秒）- 5分钟 */
        public static final int CODE_VALIDITY_SECONDS = 300;
        /** 访问令牌有效期（秒）- 1小时 */
        public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 3600;
        /** 刷新令牌有效期（秒）- 7天 */
        public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 604800;
        /** 授权码模式 */
        public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
        /** 刷新令牌模式 */
        public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
        /** 响应类型：授权码 */
        public static final String RESPONSE_TYPE_CODE = "code";
    }
    
    /**
     * 状态相关常量
     */
    public static class Status {
        /** 启用状态 */
        public static final Integer ENABLED = 1;
        /** 禁用状态 */
        public static final Integer DISABLED = 0;
        /** 未删除 */
        public static final Integer NOT_DELETED = 0;
        /** 已删除 */
        public static final Integer DELETED = 1;
    }
    
    /**
     * 缓存相关常量
     */
    public static class Cache {
        /** 缓存Key前缀：用户信息 */
        public static final String USER_INFO_PREFIX = "user:info:";
        /** 缓存Key前缀：Token */
        public static final String TOKEN_PREFIX = "token:";
        /** 缓存Key前缀：OAuth2授权码 */
        public static final String OAUTH2_CODE_PREFIX = "oauth2:code:";
        /** 缓存Key前缀：验证码 */
        public static final String CAPTCHA_PREFIX = "captcha:";
        /** 默认缓存过期时间（秒）- 1小时 */
        public static final long DEFAULT_EXPIRE_SECONDS = 3600;
        /** 短期缓存过期时间（秒）- 5分钟 */
        public static final long SHORT_EXPIRE_SECONDS = 300;
        /** 长期缓存过期时间（秒）- 7天 */
        public static final long LONG_EXPIRE_SECONDS = 604800;
    }
    
    /**
     * 平台相关常量
     */
    public static class Platform {
        /** 用户名生成：平台标识最大长度 */
        public static final int PLATFORM_SUFFIX_LENGTH = 4;
        /** 用户名生成：随机数字位数 */
        public static final int RANDOM_NUMBER_LENGTH = 6;
        /** 用户名生成：最大重试次数 */
        public static final int MAX_RETRY_TIMES = 10;
        /** 随机密码长度 */
        public static final int RANDOM_PASSWORD_LENGTH = 16;
    }
    
    /**
     * 数据库相关常量
     */
    public static class Database {
        /** 批量操作最大数量 */
        public static final int BATCH_SIZE = 1000;
        /** 分页最大每页数量 */
        public static final int MAX_PAGE_SIZE = 500;
        /** 默认每页数量 */
        public static final int DEFAULT_PAGE_SIZE = 20;
    }
}

