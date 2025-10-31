package com.myframework.ai.robot.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.myframework.ai.robot.dto.ChatRequest;
import com.myframework.ai.robot.dto.ChatResponse;
import com.myframework.ai.robot.dto.ConversationDTO;
import com.myframework.ai.robot.entity.ChatConversation;
import com.myframework.ai.robot.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI聊天控制器
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Tag(name = "AI聊天", description = "AI机器人聊天接口")
@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService chatService;

    /**
     * 发送聊天消息
     */
    @Operation(summary = "发送聊天消息")
    @PostMapping("/send")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        // 从SSO获取用户ID
        String userId = StpUtil.getLoginIdAsString();
        return chatService.chat(userId, request);
    }

    /**
     * 获取会话详情
     */
    @Operation(summary = "获取会话详情")
    @GetMapping("/conversation/{conversationId}")
    public ConversationDTO getConversation(@PathVariable String conversationId) {
        String userId = StpUtil.getLoginIdAsString();
        return chatService.getConversation(conversationId, userId);
    }

    /**
     * 获取会话列表
     */
    @Operation(summary = "获取用户的会话列表")
    @GetMapping("/conversations")
    public List<ChatConversation> listConversations() {
        String userId = StpUtil.getLoginIdAsString();
        return chatService.listConversations(userId);
    }

    /**
     * 删除会话
     */
    @Operation(summary = "删除会话")
    @DeleteMapping("/conversation/{conversationId}")
    public void deleteConversation(@PathVariable String conversationId) {
        String userId = StpUtil.getLoginIdAsString();
        chatService.deleteConversation(conversationId, userId);
    }
}

