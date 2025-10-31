# AI机器人数据库脚本说明

## 📁 文件说明

- **schema.sql** - 数据库表结构脚本
  - 创建 `ai_robot` 数据库
  - 创建所有表结构（ai_robot、ai_chat_conversation、ai_chat_message）
  - 包含详细的字段注释和索引说明

- **data.sql** - 初始化数据脚本
  - 插入默认的AI机器人配置
  - 包含5个预配置的机器人（通用助手、代码助手、写作助手、翻译助手、数据分析助手）
  - 使用 ON DUPLICATE KEY UPDATE 避免重复执行错误

## 🚀 使用方法

### 方法一：手动执行（推荐用于生产环境）

```bash
# 1. 连接MySQL
mysql -u root -p

# 2. 执行表结构脚本
source framework-ai-robot/sql/schema.sql

# 3. 执行初始化数据脚本（可选）
source framework-ai-robot/sql/data.sql
```

### 方法二：命令行一键执行

```bash
# Linux/Mac
mysql -u root -p < framework-ai-robot/sql/schema.sql
mysql -u root -p < framework-ai-robot/sql/data.sql

# 或者合并执行
cat framework-ai-robot/sql/schema.sql framework-ai-robot/sql/data.sql | mysql -u root -p
```

### 方法三：Docker自动初始化

如果使用项目的 docker-compose，数据库会在容器启动时自动初始化。

脚本位置：`docker/mysql/init/init.sql`

```bash
# 启动Docker服务（会自动执行初始化脚本）
docker-compose up -d mysql
```

## 📊 数据库结构

### 1. ai_robot - AI机器人配置表

存储AI机器人的配置信息，包括：
- 基本信息（名称、描述、头像）
- 模型配置（模型名称、温度参数、最大Token）
- 系统提示词
- 状态和权限设置

**关键字段**:
- `robot_id`: 机器人ID（主键）
- `model_name`: AI模型名称（如 gpt-3.5-turbo）
- `system_prompt`: 系统提示词（定义机器人角色）
- `temperature`: 温度参数（0.0-2.0，控制创造性）
- `max_tokens`: 最大Token数（控制回复长度）

### 2. ai_chat_conversation - 聊天会话表

管理用户与AI的对话会话：
- 会话基本信息
- 关联用户和机器人
- 会话状态和统计信息

**关键字段**:
- `conversation_id`: 会话ID（主键）
- `user_id`: 用户ID（关联SSO用户系统）
- `robot_id`: 机器人ID
- `message_count`: 消息总数

### 3. ai_chat_message - 聊天消息表

存储对话的每条消息：
- 消息内容
- 角色（user/assistant/system）
- Token统计
- 消息关联（支持对话树）

**关键字段**:
- `message_id`: 消息ID（主键）
- `conversation_id`: 会话ID
- `role`: 消息角色
- `content`: 消息内容
- `token_count`: Token使用量
- `parent_message_id`: 父消息ID（用于对话上下文）

## 🔍 数据验证

执行脚本后，可以使用以下SQL验证：

```sql
-- 切换到AI机器人数据库
USE ai_robot;

-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC ai_robot;
DESC ai_chat_conversation;
DESC ai_chat_message;

-- 查看初始化的机器人数据
SELECT robot_id, robot_name, model_name, is_public, status 
FROM ai_robot 
WHERE deleted = 0;

-- 应该看到5个预配置的机器人
-- +----------+------------------+---------------+-----------+--------+
-- | robot_id | robot_name       | model_name    | is_public | status |
-- +----------+------------------+---------------+-----------+--------+
-- | 1        | 通用助手         | gpt-3.5-turbo | 1         | 1      |
-- | 2        | 代码助手         | gpt-3.5-turbo | 1         | 1      |
-- | 3        | 写作助手         | gpt-3.5-turbo | 1         | 1      |
-- | 4        | 翻译助手         | gpt-3.5-turbo | 1         | 1      |
-- | 5        | 数据分析助手     | gpt-3.5-turbo | 1         | 1      |
-- +----------+------------------+---------------+-----------+--------+
```

## 🛠️ 数据库维护

### 定期清理历史数据

```sql
-- 删除30天前的历史消息（逻辑删除）
UPDATE ai_chat_message 
SET deleted = 1 
WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 删除已删除的空会话
UPDATE ai_chat_conversation 
SET deleted = 1 
WHERE message_count = 0 
  AND deleted = 0 
  AND create_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
```

### 数据统计

```sql
-- 统计各机器人的使用情况
SELECT 
  r.robot_name,
  COUNT(DISTINCT c.conversation_id) as conversation_count,
  COUNT(m.message_id) as message_count,
  SUM(m.token_count) as total_tokens
FROM ai_robot r
LEFT JOIN ai_chat_conversation c ON r.robot_id = c.robot_id
LEFT JOIN ai_chat_message m ON c.conversation_id = m.conversation_id
WHERE r.deleted = 0
GROUP BY r.robot_id, r.robot_name;
```

### 性能优化

```sql
-- 分析表
ANALYZE TABLE ai_robot;
ANALYZE TABLE ai_chat_conversation;
ANALYZE TABLE ai_chat_message;

-- 优化表
OPTIMIZE TABLE ai_chat_message;
```

## ⚠️ 注意事项

1. **字符集**：所有表使用 `utf8mb4` 字符集，支持emoji等特殊字符
2. **逻辑删除**：所有表都使用 `deleted` 字段进行逻辑删除，不建议物理删除数据
3. **索引优化**：已根据常用查询场景建立索引，如需自定义查询请评估是否需要新增索引
4. **数据备份**：生产环境请定期备份数据库
5. **Token统计**：建议定期统计Token使用量，控制成本

## 📝 修改建议

如果需要修改表结构，建议：

1. 在开发环境测试
2. 编写迁移脚本（migration script）
3. 备份生产数据
4. 在维护窗口执行

示例迁移脚本：

```sql
-- migration_001_add_field.sql
-- 添加新字段示例
ALTER TABLE ai_robot 
ADD COLUMN new_field VARCHAR(256) DEFAULT NULL COMMENT '新字段' 
AFTER max_tokens;
```

## 🔗 相关文档

- [部署指南](../docs/DEPLOYMENT.md)
- [API文档](../docs/API.md)
- [项目README](../README.md)

