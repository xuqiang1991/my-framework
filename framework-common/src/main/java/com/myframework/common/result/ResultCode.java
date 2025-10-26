package com.myframework.common.result;

import lombok.Getter;

/**
 * 统一状态码枚举
 */
@Getter
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    // 服务端错误
    ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务错误码
    TOKEN_EXPIRED(1001, "Token已过期"),
    TOKEN_INVALID(1002, "Token无效"),
    USER_NOT_FOUND(1003, "用户不存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    USER_DISABLED(1005, "用户已被禁用"),
    USER_EXISTS(1006, "用户已存在"),
    
    // 参数校验错误
    PARAM_VALID_ERROR(2001, "参数校验失败"),
    PARAM_MISSING(2002, "缺少必要参数"),
    
    // 数据库错误
    DB_ERROR(3001, "数据库操作失败"),
    DATA_NOT_FOUND(3002, "数据不存在"),
    DATA_EXISTS(3003, "数据已存在"),
    
    // 缓存错误
    REDIS_ERROR(4001, "Redis操作失败");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 消息
     */
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

