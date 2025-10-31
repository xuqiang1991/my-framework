package com.myframework.ai.robot.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myframework.ai.robot.dto.ChatRequest;
import com.myframework.ai.robot.dto.ChatResponse;
import com.myframework.ai.robot.dto.ConversationDTO;
import com.myframework.ai.robot.entity.AiRobot;
import com.myframework.ai.robot.entity.ChatConversation;
import com.myframework.ai.robot.entity.ChatMessage;
import com.myframework.ai.robot.mapper.AiRobotMapper;
import com.myframework.ai.robot.mapper.ChatConversationMapper;
import com.myframework.ai.robot.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// TODO: 临时注释Spring AI导入，等API稳定后再启用
// import org.springframework.ai.chat.messages.AssistantMessage;
// import org.springframework.ai.chat.messages.Message;
// import org.springframework.ai.chat.messages.SystemMessage;
// import org.springframework.ai.chat.messages.UserMessage;
// import org.springframework.ai.chat.model.ChatResponse;
// import org.springframework.ai.chat.prompt.Prompt;
// import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI聊天服务
 * 处理与AI的对话交互逻辑
 * 
 * @author MyFramework
 * @since 2025-10-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    // TODO: 临时注释，等Spring AI API稳定后再启用
    // private final OpenAiChatModel chatModel;
    private final AiRobotMapper robotMapper;
    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;

    /**
     * 发送聊天消息
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatResponse chat(String userId, ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 1. 获取或创建会话
        ChatConversation conversation = getOrCreateConversation(userId, request);
        
        // 2. 获取机器人配置
        AiRobot robot = robotMapper.selectById(request.getRobotId());
        if (robot == null || robot.getStatus() == 0) {
            throw new RuntimeException("机器人不存在或已禁用");
        }
        
        // 3. 保存用户消息
        ChatMessage userMessage = saveUserMessage(conversation.getConversationId(), request.getMessage());
        
        // TODO: 临时使用模拟响应，等Spring AI API稳定后再启用真实AI调用
        // 4. 构建对话上下文
        // List<Message> messages = buildConversationContext(conversation, robot, request.getMessage());
        
        // 5. 调用AI模型（临时模拟）
        log.warn("AI功能暂未启用，返回模拟响应");
        String aiResponse = generateMockResponse(request.getMessage(), robot);
        
        // 6. 保存AI回复
        ChatMessage assistantMessage = saveAssistantMessage(
            conversation.getConversationId(), 
            aiResponse, 
            robot.getModelName(),
            userMessage.getMessageId()
        );
        
        // 7. 更新会话
        updateConversation(conversation);
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        return ChatResponse.builder()
            .conversationId(conversation.getConversationId())
            .messageId(assistantMessage.getMessageId())
            .content(aiResponse)
            .modelName(robot.getModelName())
            .responseTime(responseTime)
            .build();
    }

    /**
     * 获取会话详情
     */
    public ConversationDTO getConversation(String conversationId, String userId) {
        // 查询会话
        ChatConversation conversation = conversationMapper.selectOne(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getConversationId, conversationId)
                .eq(ChatConversation::getUserId, userId)
        );
        
        if (conversation == null) {
            throw new RuntimeException("会话不存在");
        }
        
        // 查询机器人信息
        AiRobot robot = robotMapper.selectById(conversation.getRobotId());
        
        // 查询消息列表
        List<ChatMessage> messages = messageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
        );
        
        // 组装DTO
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId(conversation.getConversationId());
        dto.setTitle(conversation.getTitle());
        dto.setMessageCount(conversation.getMessageCount());
        dto.setCreateTime(conversation.getCreateTime());
        dto.setUpdateTime(conversation.getUpdateTime());
        
        // 机器人信息
        if (robot != null) {
            ConversationDTO.RobotInfo robotInfo = new ConversationDTO.RobotInfo();
            robotInfo.setRobotId(robot.getRobotId());
            robotInfo.setRobotName(robot.getRobotName());
            robotInfo.setRobotAvatar(robot.getRobotAvatar());
            dto.setRobot(robotInfo);
        }
        
        // 消息列表
        List<ConversationDTO.MessageDTO> messageDTOs = messages.stream().map(msg -> {
            ConversationDTO.MessageDTO messageDTO = new ConversationDTO.MessageDTO();
            messageDTO.setMessageId(msg.getMessageId());
            messageDTO.setRole(msg.getRole());
            messageDTO.setContent(msg.getContent());
            messageDTO.setTokenCount(msg.getTokenCount());
            messageDTO.setCreateTime(msg.getCreateTime());
            return messageDTO;
        }).collect(Collectors.toList());
        dto.setMessages(messageDTOs);
        
        return dto;
    }

    /**
     * 获取用户的会话列表
     */
    public List<ChatConversation> listConversations(String userId) {
        return conversationMapper.selectList(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getUserId, userId)
                .orderByDesc(ChatConversation::getUpdateTime)
        );
    }

    /**
     * 删除会话
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(String conversationId, String userId) {
        // 验证会话所属
        ChatConversation conversation = conversationMapper.selectOne(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getConversationId, conversationId)
                .eq(ChatConversation::getUserId, userId)
        );
        
        if (conversation == null) {
            throw new RuntimeException("会话不存在");
        }
        
        // 逻辑删除会话
        conversationMapper.deleteById(conversationId);
        
        // 逻辑删除消息
        messageMapper.delete(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
        );
    }

    /**
     * 获取或创建会话
     */
    private ChatConversation getOrCreateConversation(String userId, ChatRequest request) {
        if (StrUtil.isNotBlank(request.getConversationId())) {
            ChatConversation conversation = conversationMapper.selectById(request.getConversationId());
            if (conversation != null && conversation.getUserId().equals(userId)) {
                return conversation;
            }
        }
        
        // 创建新会话
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationId(IdUtil.getSnowflakeNextIdStr());
        conversation.setUserId(userId);
        conversation.setRobotId(request.getRobotId());
        conversation.setTitle("新对话");
        conversation.setStatus(1);
        conversation.setMessageCount(0);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.insert(conversation);
        
        return conversation;
    }

    /**
     * 保存用户消息
     */
    private ChatMessage saveUserMessage(String conversationId, String content) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(IdUtil.getSnowflakeNextIdStr());
        message.setConversationId(conversationId);
        message.setRole("user");
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    /**
     * 保存AI助手消息
     */
    private ChatMessage saveAssistantMessage(String conversationId, String content, 
                                             String modelName, String parentMessageId) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(IdUtil.getSnowflakeNextIdStr());
        message.setConversationId(conversationId);
        message.setRole("assistant");
        message.setContent(content);
        message.setModelName(modelName);
        message.setParentMessageId(parentMessageId);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    /**
     * 生成模拟AI响应（临时方法）
     * TODO: 等Spring AI API稳定后，替换为真实的AI调用
     */
    private String generateMockResponse(String userMessage, AiRobot robot) {
        return String.format("您好！我是%s。您的消息是：\"%s\"。\n\n" +
            "⚠️ 提示：AI功能目前正在维护中，这是一个模拟响应。\n" +
            "Spring AI框架API正在更新中，预计在1.0.0正式版发布后将恢复完整功能。\n\n" +
            "当前您仍然可以：\n" +
            "1. 创建和管理AI机器人配置\n" +
            "2. 查看和管理对话会话\n" +
            "3. 测试其他服务功能",
            robot.getRobotName(), userMessage);
    }
    
    /**
     * 构建对话上下文（临时禁用）
     * TODO: 等Spring AI API稳定后再启用
     */
    /* private List<Message> buildConversationContext(ChatConversation conversation, 
                                                   AiRobot robot, String userMessage) {
        List<Message> messages = new ArrayList<>();
        
        // 添加系统提示词
        if (StrUtil.isNotBlank(robot.getSystemPrompt())) {
            messages.add(new SystemMessage(robot.getSystemPrompt()));
        }
        
        // 获取历史消息（最近10条）
        List<ChatMessage> historyMessages = messageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversation.getConversationId())
                .orderByDesc(ChatMessage::getCreateTime())
                .last("LIMIT 10")
        );
        
        // 按时间正序排列
        historyMessages.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        
        // 添加历史消息
        for (ChatMessage msg : historyMessages) {
            if ("user".equals(msg.getRole())) {
                messages.add(new UserMessage(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        
        // 添加当前用户消息
        messages.add(new UserMessage(userMessage));
        
        return messages;
    } */

    /**
     * 更新会话信息
     */
    private void updateConversation(ChatConversation conversation) {
        conversation.setMessageCount(conversation.getMessageCount() + 2); // 用户消息+AI回复
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);
    }
}

