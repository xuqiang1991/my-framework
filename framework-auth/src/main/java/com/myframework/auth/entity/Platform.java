package com.myframework.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台实体类
 */
@Data
@TableName("sys_platform")
public class Platform implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 平台ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

