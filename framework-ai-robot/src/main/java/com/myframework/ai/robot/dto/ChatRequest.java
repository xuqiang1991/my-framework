package com.myframework.ai.robot.dto;

import lombok.Data;

/**
 * 聊天请求DTO
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
public class ChatRequest {

    /**
     * 会话ID（如果为空则创建新会话）
     */
    private String conversationId;

    /**
     * 机器人ID
     */
    private String robotId;

    /**
     * 用户消息内容
     */
    private String message;

    /**
     * 是否流式输出
     */
    private Boolean stream = false;

    /**
     * 温度参数（可选，覆盖机器人默认配置）
     */
    private Double temperature;

    /**
     * 最大Token数（可选，覆盖机器人默认配置）
     */
    private Integer maxTokens;
}

