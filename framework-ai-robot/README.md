# AI机器人服务 (framework-ai-robot)

## 📝 服务简介

基于Spring AI框架构建的智能对话机器人服务，提供AI对话、机器人管理等功能。服务独立部署，使用独立数据库，通过SSO实现用户认证。

## 🎯 核心特性

- **Spring AI集成**：基于Spring AI框架，支持多种AI模型
- **多机器人管理**：支持创建和管理多个AI机器人，每个机器人可配置独立的参数
- **会话管理**：完整的对话会话管理，支持历史记录查询
- **SSO认证**：统一使用框架的SSO认证系统
- **独立数据库**：使用独立的`ai_robot`数据库，数据隔离
- **RESTful API**：提供标准的REST接口
- **API文档**：集成Knife4j，提供完整的API文档

## 🏗️ 技术栈

- **Spring Boot 3.2.5** - 应用框架
- **Spring AI 1.0.0-M3** - AI集成框架
- **MyBatis Plus** - 数据持久化
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **Nacos** - 服务注册与配置中心
- **Sa-Token** - 权限认证
- **Knife4j** - API文档

## 📦 数据库设计

### 核心表结构

1. **ai_robot** - AI机器人配置表
   - 存储机器人的基本信息、模型配置、系统提示词等

2. **ai_chat_conversation** - 聊天会话表
   - 管理用户与AI的对话会话

3. **ai_chat_message** - 聊天消息表
   - 存储每条对话消息的详细内容

## 🚀 快速开始

### 1. 环境准备

需要配置OpenAI API Key（或兼容的API服务）：

```bash
# 设置环境变量
export OPENAI_API_KEY=sk-your-api-key-here
export OPENAI_BASE_URL=https://api.openai.com  # 可选，默认为OpenAI官方地址
```

### 2. 本地开发

```bash
# 启动依赖服务（MySQL、Redis、Nacos）
docker-compose up -d mysql redis nacos

# 运行服务
cd framework-ai-robot
mvn spring-boot:run
```

### 3. Docker部署

```bash
# 构建并启动所有服务（包括AI机器人服务）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看AI机器人服务日志
docker-compose logs -f ai-robot
```

## 📡 API接口

### 服务端口
- **服务端口**: 8083
- **调试端口**: 5008
- **API文档**: http://localhost:8083/doc.html

### 主要接口

#### 1. AI聊天接口

**发送聊天消息**
```http
POST /api/ai/chat/send
Content-Type: application/json
Authorization: Bearer {token}

{
  "conversationId": "会话ID（可选，不传则创建新会话）",
  "robotId": "1",
  "message": "你好，请介绍一下你自己",
  "stream": false
}
```

**获取会话列表**
```http
GET /api/ai/chat/conversations
Authorization: Bearer {token}
```

**获取会话详情**
```http
GET /api/ai/chat/conversation/{conversationId}
Authorization: Bearer {token}
```

**删除会话**
```http
DELETE /api/ai/chat/conversation/{conversationId}
Authorization: Bearer {token}
```

#### 2. 机器人管理接口

**获取公开机器人列表**
```http
GET /api/ai/robot/public
```

**获取我的机器人列表**
```http
GET /api/ai/robot/my
Authorization: Bearer {token}
```

**创建机器人**
```http
POST /api/ai/robot
Content-Type: application/json
Authorization: Bearer {token}

{
  "robotName": "我的助手",
  "robotDesc": "一个专属的AI助手",
  "modelName": "gpt-3.5-turbo",
  "systemPrompt": "你是一个友好的助手",
  "temperature": 0.7,
  "maxTokens": 2000,
  "isPublic": 0
}
```

**更新机器人**
```http
PUT /api/ai/robot
Content-Type: application/json
Authorization: Bearer {token}

{
  "robotId": "1",
  "robotName": "更新后的名称",
  ...
}
```

**删除机器人**
```http
DELETE /api/ai/robot/{robotId}
Authorization: Bearer {token}
```

## 🔧 配置说明

### application.yml 主要配置项

```yaml
spring:
  # 数据库配置（独立数据库）
  datasource:
    url: jdbc:mysql://localhost:3306/ai_robot
    
  # Spring AI配置
  ai:
    openai:
      api-key: ${SPRING_AI_OPENAI_API_KEY}
      base-url: ${SPRING_AI_OPENAI_BASE_URL}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 2000
```

### 支持的AI模型

支持所有OpenAI兼容的API，包括但不限于：
- OpenAI GPT-3.5/GPT-4
- Azure OpenAI
- 阿里云通义千问
- 智谱AI
- 其他兼容OpenAI API格式的服务

只需修改`base-url`和`api-key`即可切换不同的服务商。

## 🔐 认证说明

本服务使用框架统一的SSO认证系统：

1. 用户通过认证服务（framework-auth）登录
2. 获取JWT Token
3. 在请求头中携带Token访问AI机器人服务
4. 服务会通过Sa-Token验证用户身份

## 📊 监控与日志

- **健康检查**: http://localhost:8083/actuator/health
- **Prometheus指标**: http://localhost:8083/actuator/prometheus
- **日志文件**: logs/framework-ai-robot.log

## 🛠️ 开发指南

### 项目结构

```
framework-ai-robot/
├── src/main/java/com/myframework/ai/robot/
│   ├── AiRobotApplication.java       # 启动类
│   ├── config/                       # 配置类
│   ├── controller/                   # 控制器
│   │   ├── AiChatController.java    # 聊天接口
│   │   └── AiRobotController.java   # 机器人管理接口
│   ├── service/                      # 服务层
│   │   ├── AiChatService.java       # 聊天服务
│   │   └── AiRobotService.java      # 机器人管理服务
│   ├── entity/                       # 实体类
│   ├── mapper/                       # 数据访问层
│   └── dto/                          # 数据传输对象
└── src/main/resources/
    ├── application.yml               # 应用配置
    └── bootstrap.yml                 # 启动配置
```

### 扩展功能

可以根据需要扩展以下功能：
- 流式输出支持
- 知识库集成（Vector Store）
- 多模态支持（图片、音频）
- 函数调用（Function Calling）
- 对话插件系统

## 📝 注意事项

1. **API Key安全**：生产环境中请妥善保管API Key，建议使用环境变量或配置中心
2. **成本控制**：AI模型调用会产生费用，建议设置请求限流和Token限制
3. **数据隔离**：使用独立数据库，避免与其他服务数据混淆
4. **错误处理**：建议在业务代码中增加完善的错误处理和重试机制

## 🔐 认证说明

AI机器人服务使用**SSO（OAuth2）认证**，不提供直接登录接口。需要通过OAuth2授权流程获取访问令牌。

**快速开始**：
1. 引导用户到授权页面：`http://localhost:8081/oauth2/authorize?client_id=ai-robot-client&redirect_uri=http://localhost:8083/callback&response_type=code&scope=read,write,user_info,ai_chat`
2. 用户登录后获取授权码
3. 使用授权码换取访问令牌：`POST http://localhost:8081/oauth2/token`
4. 使用访问令牌调用API：`Authorization: Bearer {access_token}`

**客户端信息**：
- 客户端ID: `ai-robot-client`
- 客户端密钥: `secret123`

详细认证流程请参考：[认证指南](./docs/AUTHENTICATION.md)

## 📚 更多文档

- **[部署指南](./docs/DEPLOYMENT.md)** - 详细的部署和配置说明
- **[API文档](./docs/API.md)** - 完整的API接口文档
- **[认证指南](./docs/AUTHENTICATION.md)** - OAuth2认证详细说明
- **[数据库脚本](./sql/)** - 数据库初始化和数据脚本

## 🔗 相关链接

- [Spring AI官方文档](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API文档](https://platform.openai.com/docs)
- [项目主文档](../README.md)

## 📄 许可证

与主项目保持一致

