-- ==========================================
-- AI机器人初始化数据脚本
-- ==========================================
-- 说明：本脚本用于插入测试数据和初始化数据
-- 执行前提：已执行 schema.sql 创建表结构
-- ==========================================

USE `ai_robot`;

-- ==========================================
-- 清空已有数据（可选，仅在重新初始化时使用）
-- ==========================================
-- TRUNCATE TABLE `ai_chat_message`;
-- TRUNCATE TABLE `ai_chat_conversation`;
-- TRUNCATE TABLE `ai_robot`;

-- ==========================================
-- 初始化AI机器人配置
-- ==========================================

-- 1. 通用助手 - 适合日常对话
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
)
ON DUPLICATE KEY UPDATE
  `robot_name` = VALUES(`robot_name`),
  `robot_desc` = VALUES(`robot_desc`),
  `update_time` = CURRENT_TIMESTAMP;

-- 2. 代码助手 - 专注编程和技术
INSERT INTO `ai_robot` (`robot_id`, `robot_name`, `robot_desc`, `robot_avatar`, `model_name`, `system_prompt`, `temperature`, `max_tokens`, `status`, `is_public`, `create_by`, `remark`)
VALUES 
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
)
ON DUPLICATE KEY UPDATE
  `robot_name` = VALUES(`robot_name`),
  `robot_desc` = VALUES(`robot_desc`),
  `update_time` = CURRENT_TIMESTAMP;

-- 3. 写作助手 - 文案创作
INSERT INTO `ai_robot` (`robot_id`, `robot_name`, `robot_desc`, `robot_avatar`, `model_name`, `system_prompt`, `temperature`, `max_tokens`, `status`, `is_public`, `create_by`, `remark`)
VALUES 
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
)
ON DUPLICATE KEY UPDATE
  `robot_name` = VALUES(`robot_name`),
  `robot_desc` = VALUES(`robot_desc`),
  `update_time` = CURRENT_TIMESTAMP;

-- ==========================================
-- 更多机器人模板（可选）
-- ==========================================

-- 4. 翻译助手 - 多语言翻译
INSERT INTO `ai_robot` (`robot_id`, `robot_name`, `robot_desc`, `robot_avatar`, `model_name`, `system_prompt`, `temperature`, `max_tokens`, `status`, `is_public`, `create_by`, `remark`)
VALUES 
(
  '4', 
  '翻译助手', 
  '专业的多语言翻译助手，支持中英日韩等多种语言互译。提供准确、流畅的翻译服务。', 
  'https://api.dicebear.com/7.x/bottts/svg?seed=translator', 
  'gpt-3.5-turbo', 
  '你是一个专业的翻译助手，精通中文、英语、日语、韩语等多种语言。请提供准确、地道、流畅的翻译。翻译时要注意语境和文化差异，确保译文符合目标语言的表达习惯。', 
  0.3, 
  2000, 
  1, 
  1, 
  '1',
  '系统默认机器人'
)
ON DUPLICATE KEY UPDATE
  `robot_name` = VALUES(`robot_name`),
  `robot_desc` = VALUES(`robot_desc`),
  `update_time` = CURRENT_TIMESTAMP;

-- 5. 数据分析助手
INSERT INTO `ai_robot` (`robot_id`, `robot_name`, `robot_desc`, `robot_avatar`, `model_name`, `system_prompt`, `temperature`, `max_tokens`, `status`, `is_public`, `create_by`, `remark`)
VALUES 
(
  '5', 
  '数据分析助手', 
  '专注于数据分析和统计的AI助手。擅长数据解读、趋势分析、可视化建议等。', 
  'https://api.dicebear.com/7.x/bottts/svg?seed=data', 
  'gpt-3.5-turbo', 
  '你是一个专业的数据分析助手，擅长数据统计、趋势分析、数据可视化。请用清晰的逻辑分析数据，提供有价值的洞察。在回答时，尽可能使用图表、表格等方式呈现数据，让分析结果更直观。', 
  0.4, 
  2500, 
  1, 
  1, 
  '1',
  '系统默认机器人'
)
ON DUPLICATE KEY UPDATE
  `robot_name` = VALUES(`robot_name`),
  `robot_desc` = VALUES(`robot_desc`),
  `update_time` = CURRENT_TIMESTAMP;

-- ==========================================
-- 测试数据（可选，用于开发测试）
-- ==========================================

-- 插入测试会话（需要确保用户ID "1" 存在于SSO系统）
-- INSERT INTO `ai_chat_conversation` (`conversation_id`, `user_id`, `robot_id`, `title`, `status`, `message_count`, `create_time`, `update_time`)
-- VALUES 
-- ('test_conv_1', '1', '1', '测试对话', 1, 0, NOW(), NOW());

-- 插入测试消息
-- INSERT INTO `ai_chat_message` (`message_id`, `conversation_id`, `role`, `content`, `token_count`, `model_name`, `create_time`)
-- VALUES 
-- ('test_msg_1', 'test_conv_1', 'user', '你好，请介绍一下你自己', 10, 'gpt-3.5-turbo', NOW()),
-- ('test_msg_2', 'test_conv_1', 'assistant', '你好！我是通用助手，一个友好、专业的AI助手。我可以帮助你回答各种问题...', 50, 'gpt-3.5-turbo', NOW());

-- ==========================================
-- 数据验证查询
-- ==========================================

-- 查询所有机器人
-- SELECT * FROM `ai_robot` WHERE `deleted` = 0 ORDER BY `create_time` DESC;

-- 查询公开的机器人
-- SELECT * FROM `ai_robot` WHERE `deleted` = 0 AND `is_public` = 1 AND `status` = 1;

-- 查询会话统计
-- SELECT COUNT(*) as total_conversations FROM `ai_chat_conversation` WHERE `deleted` = 0;

-- 查询消息统计
-- SELECT COUNT(*) as total_messages FROM `ai_chat_message` WHERE `deleted` = 0;

-- ==========================================
-- 说明
-- ==========================================
-- 1. 所有INSERT语句都使用了 ON DUPLICATE KEY UPDATE，避免重复执行时报错
-- 2. 机器人ID使用简单的数字ID，便于记忆和测试
-- 3. temperature参数说明：
--    - 0.0-0.3: 非常确定性，适合翻译、代码等需要准确性的场景
--    - 0.4-0.7: 平衡创造性和准确性，适合通用对话
--    - 0.8-1.0: 更有创造性，适合写作、创意等场景
-- 4. max_tokens参数根据不同场景调整，代码生成通常需要更多tokens

