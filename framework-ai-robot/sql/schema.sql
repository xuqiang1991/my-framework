-- ==========================================
-- AI机器人数据库初始化脚本
-- ==========================================
-- 说明：本脚本用于初始化AI机器人服务的数据库
-- 数据库名称：ai_robot
-- 字符集：utf8mb4
-- 排序规则：utf8mb4_unicode_ci
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ai_robot` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ai_robot`;

-- ==========================================
-- 1. AI机器人配置表
-- ==========================================
-- 用于存储AI机器人的配置信息，包括模型参数、提示词等
CREATE TABLE IF NOT EXISTS `ai_robot` (
  `robot_id` varchar(64) NOT NULL COMMENT '机器人ID',
  `robot_name` varchar(128) NOT NULL COMMENT '机器人名称',
  `robot_desc` varchar(512) DEFAULT NULL COMMENT '机器人描述',
  `robot_avatar` varchar(512) DEFAULT NULL COMMENT '机器人头像URL',
  `model_name` varchar(64) DEFAULT 'gpt-3.5-turbo' COMMENT 'AI模型名称（如：gpt-3.5-turbo, gpt-4等）',
  `system_prompt` text COMMENT '系统提示词（定义机器人的角色和行为）',
  `temperature` decimal(3,2) DEFAULT 0.70 COMMENT '温度参数（0.0-2.0，控制输出随机性）',
  `max_tokens` int(11) DEFAULT 2000 COMMENT '最大Token数（控制回复长度）',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `is_public` tinyint(1) DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者ID（关联用户ID）',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`robot_id`),
  KEY `idx_create_by` (`create_by`) COMMENT '创建者索引',
  KEY `idx_is_public` (`is_public`) COMMENT '公开状态索引',
  KEY `idx_status` (`status`) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI机器人配置表';

-- ==========================================
-- 2. 聊天会话表
-- ==========================================
-- 用于管理用户与AI机器人的对话会话
CREATE TABLE IF NOT EXISTS `ai_chat_conversation` (
  `conversation_id` varchar(64) NOT NULL COMMENT '会话ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID（关联SSO用户系统）',
  `robot_id` varchar(64) NOT NULL COMMENT '机器人ID',
  `title` varchar(256) DEFAULT '新对话' COMMENT '会话标题',
  `status` tinyint(1) DEFAULT 1 COMMENT '会话状态：0-已结束，1-进行中',
  `message_count` int(11) DEFAULT 0 COMMENT '消息总数',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`conversation_id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_robot_id` (`robot_id`) COMMENT '机器人ID索引',
  KEY `idx_update_time` (`update_time`) COMMENT '更新时间索引（用于排序）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- ==========================================
-- 3. 聊天消息表
-- ==========================================
-- 用于存储每条对话消息的详细内容
CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `message_id` varchar(64) NOT NULL COMMENT '消息ID',
  `conversation_id` varchar(64) NOT NULL COMMENT '会话ID',
  `role` varchar(32) NOT NULL COMMENT '消息角色：user-用户，assistant-AI助手，system-系统',
  `content` text NOT NULL COMMENT '消息内容',
  `token_count` int(11) DEFAULT 0 COMMENT 'Token使用量（用于成本统计）',
  `model_name` varchar(64) DEFAULT NULL COMMENT '使用的模型名称',
  `parent_message_id` varchar(64) DEFAULT NULL COMMENT '父消息ID（用于追踪对话上下文）',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`message_id`),
  KEY `idx_conversation_id` (`conversation_id`) COMMENT '会话ID索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_parent_message_id` (`parent_message_id`) COMMENT '父消息ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- ==========================================
-- 初始化数据
-- ==========================================

-- 初始化默认AI机器人
-- 注意：这里的头像URL使用了DiceBear API，可以生成各种风格的机器人头像
INSERT INTO `ai_robot` (`robot_id`, `robot_name`, `robot_desc`, `robot_avatar`, `model_name`, `system_prompt`, `temperature`, `max_tokens`, `status`, `is_public`, `create_by`, `remark`)
VALUES 
(
  '1', 
  '通用助手', 
  '一个友好、专业的AI助手，可以回答各种问题。适合日常对话、知识问答、建议咨询等场景。', 
  'https://api.dicebear.com/7.x/bottts/svg?seed=general', 
  'gpt-3.5-turbo', 
  '你是一个友好、专业的AI助手。请用简洁、准确的语言回答用户的问题。如果不确定答案，请诚实地说明。始终保持礼貌和专业的态度。', 
  0.7, 
  2000, 
  1, 
  1, 
  '1',
  '系统默认机器人'
),
(
  '2', 
  '代码助手', 
  '专注于编程和技术问题的AI助手。擅长代码编写、调试、优化和技术解答。支持多种编程语言和框架。', 
  'https://api.dicebear.com/7.x/bottts/svg?seed=code', 
  'gpt-3.5-turbo', 
  '你是一个专业的编程助手，擅长各种编程语言和技术框架。请提供清晰的代码示例和技术解释。代码应该遵循最佳实践，并包含必要的注释。在回答技术问题时，要准确、详细，并给出实用的建议。', 
  0.5, 
  3000, 
  1, 
  1, 
  '1',
  '系统默认机器人'
),
(
  '3', 
  '写作助手', 
  '帮助用户进行文案创作和写作的AI助手。擅长各种文体创作、文字优化、创意激发等。', 
  'https://api.dicebear.com/7.x/bottts/svg?seed=writer', 
  'gpt-3.5-turbo', 
  '你是一个专业的写作助手，擅长各种文体的创作。请帮助用户优化文字表达，提供创意灵感，并确保内容流畅、准确、富有表现力。在创作时注意语言的美感和节奏，使文字更加生动有趣。', 
  0.8, 
  2500, 
  1, 
  1, 
  '1',
  '系统默认机器人'
);

-- ==========================================
-- 索引说明
-- ==========================================
-- 1. PRIMARY KEY: 主键索引，确保数据唯一性
-- 2. idx_user_id: 用户查询会话列表时使用
-- 3. idx_conversation_id: 查询会话消息时使用
-- 4. idx_update_time: 会话列表排序时使用
-- 5. idx_create_by: 查询用户创建的机器人时使用
-- 6. idx_is_public: 查询公开机器人时使用

-- ==========================================
-- 表结构说明
-- ==========================================
-- 1. 所有表都使用逻辑删除（deleted字段）
-- 2. 使用雪花ID作为主键（varchar(64)）
-- 3. 时间字段使用datetime类型
-- 4. 使用utf8mb4字符集，支持emoji等特殊字符
-- 5. 状态字段统一使用tinyint(1)类型

-- ==========================================
-- 数据库维护建议
-- ==========================================
-- 1. 定期清理历史消息数据，避免表过大
-- 2. 根据实际使用情况调整索引
-- 3. 定期备份数据库
-- 4. 监控Token使用量，控制成本
-- 5. 建议对content字段建立全文索引（如需要搜索功能）

