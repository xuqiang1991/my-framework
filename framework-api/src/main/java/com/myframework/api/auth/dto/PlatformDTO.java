package com.myframework.api.auth.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台DTO
 */
@Data
public class PlatformDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 平台ID
     */
    private String platformId;
    
    /**
     * 平台编码
     */
    private String platformCode;
    
    /**
     * 平台名称
     */
    private String platformName;
    
    /**
     * 平台地址
     */
    private String platformUrl;
    
    /**
     * 平台描述
     */
    private String platformDesc;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

