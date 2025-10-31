-- ==========================================
-- AI机器人服务 OAuth2 客户端配置
-- ==========================================
-- 说明：为AI机器人服务注册OAuth2客户端，使其可以通过SSO认证
-- ==========================================

USE `my_framework`;

-- 创建AI机器人平台（如果不存在）
INSERT INTO `sys_platform` (`platform_id`, `platform_code`, `platform_name`, `platform_url`, `platform_desc`, `status`)
VALUES 
('4', 'AI_ROBOT', 'AI机器人服务', 'http://localhost:8083', 'AI机器人服务平台', 1)
ON DUPLICATE KEY UPDATE 
  `platform_name` = VALUES(`platform_name`),
  `platform_url` = VALUES(`platform_url`),
  `platform_desc` = VALUES(`platform_desc`);

-- 注册AI机器人服务的OAuth2客户端
-- 客户端ID: ai-robot-client
-- 客户端密钥: secret123 (BCrypt加密后的值)
INSERT INTO `sys_oauth_client` (
  `client_id`, 
  `client_secret`, 
  `client_name`, 
  `platform_id`, 
  `redirect_uris`, 
  `grant_types`, 
  `scope`, 
  `access_token_validity`,
  `refresh_token_validity`,
  `auto_approve`,
  `status`
) VALUES (
  'ai-robot-client',
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', -- 明文密码: secret123
  'AI机器人服务',
  '4',
  'http://localhost:8083/callback,http://localhost:8083/callback.html,http://localhost:8083/login/callback',
  'authorization_code,refresh_token',
  'read,write,user_info,ai_chat',
  3600,
  604800,
  1,
  1
)
ON DUPLICATE KEY UPDATE 
  `client_name` = VALUES(`client_name`),
  `redirect_uris` = VALUES(`redirect_uris`),
  `scope` = VALUES(`scope`),
  `update_time` = CURRENT_TIMESTAMP;

-- ==========================================
-- 使用说明
-- ==========================================
-- 1. 客户端ID: ai-robot-client
-- 2. 客户端密钥: secret123
-- 3. 授权端点: http://localhost:8081/oauth2/authorize
-- 4. 令牌端点: http://localhost:8081/oauth2/token
-- 5. 用户信息端点: http://localhost:8081/oauth2/userinfo
-- 
-- OAuth2授权流程：
-- 1. 引导用户访问: http://localhost:8081/oauth2/authorize?client_id=ai-robot-client&redirect_uri=http://localhost:8083/callback&response_type=code&scope=read,write,user_info,ai_chat
-- 2. 用户在SSO页面登录
-- 3. 获取授权码，重定向到: http://localhost:8083/callback?code=xxx&state=xxx
-- 4. 使用授权码换取访问令牌: POST http://localhost:8081/oauth2/token
-- 5. 使用访问令牌调用AI机器人服务API
-- ==========================================

