package com.myframework.ai.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.ai.robot.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天消息Mapper
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}

