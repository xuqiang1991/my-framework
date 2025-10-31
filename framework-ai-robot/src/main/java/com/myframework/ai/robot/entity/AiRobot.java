package com.myframework.ai.robot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI机器人实体
 * 用于配置和管理不同的AI机器人实例
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
@TableName("ai_robot")
public class AiRobot {

    /**
     * 机器人ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String robotId;

    /**
     * 机器人名称
     */
    private String robotName;

    /**
     * 机器人描述
     */
    private String robotDesc;

    /**
     * 机器人头像
     */
    private String robotAvatar;

    /**
     * 模型名称（如：gpt-4, gpt-3.5-turbo等）
     */
    private String modelName;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 温度参数（0.0-2.0，控制输出随机性）
     */
    private Double temperature;

    /**
     * 最大Token数
     */
    private Integer maxTokens;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 是否公开：0-私有，1-公开
     */
    private Integer isPublic;

    /**
     * 创建者ID
     */
    private String createBy;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}

