package com.myframework.ai.robot.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话详情DTO
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Data
public class ConversationDTO {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 机器人信息
     */
    private RobotInfo robot;

    /**
     * 消息列表
     */
    private List<MessageDTO> messages;

    /**
     * 消息总数
     */
    private Integer messageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Data
    public static class RobotInfo {
        private String robotId;
        private String robotName;
        private String robotAvatar;
    }

    @Data
    public static class MessageDTO {
        private String messageId;
        private String role;
        private String content;
        private Integer tokenCount;
        private LocalDateTime createTime;
    }
}

