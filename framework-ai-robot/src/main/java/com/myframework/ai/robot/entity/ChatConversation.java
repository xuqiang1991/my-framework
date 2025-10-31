package com.myframework.ai.robot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话实体
 * 用于存储用户与AI机器人的对话会话
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
@TableName("ai_chat_conversation")
public class ChatConversation {

    /**
     * 会话ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String conversationId;

    /**
     * 用户ID（关联SSO用户系统）
     */
    private String userId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 机器人ID
     */
    private String robotId;

    /**
     * 会话状态：0-已结束，1-进行中
     */
    private Integer status;

    /**
     * 消息总数
     */
    private Integer messageCount;

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

