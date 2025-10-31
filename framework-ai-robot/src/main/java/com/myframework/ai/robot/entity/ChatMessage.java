package com.myframework.ai.robot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 * 用于存储每条对话消息的详细内容
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
@TableName("ai_chat_message")
public class ChatMessage {

    /**
     * 消息ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String messageId;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 消息角色：user-用户，assistant-AI助手，system-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * Token使用量
     */
    private Integer tokenCount;

    /**
     * 模型名称
     */
    private String modelName;

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
     * 父消息ID（用于追踪对话上下文）
     */
    private String parentMessageId;
}

