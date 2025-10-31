package com.myframework.ai.robot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应DTO
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * AI回复内容
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
     * 响应时间（毫秒）
     */
    private Long responseTime;
}

