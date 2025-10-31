package com.myframework.ai.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myframework.ai.robot.entity.ChatConversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天会话Mapper
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {
}

