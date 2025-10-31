# AI机器人服务故障排除

## 🚨 当前问题：Docker 构建失败

### 问题描述

AI机器人服务在 Docker 构建时出现编译错误：

```
package org.springframework.ai.chat.model does not exist
package org.springframework.ai.chat.client does not exist
```

### 根本原因

Spring AI 项目还在快速发展中，Milestone 版本之间的 API 变化较大，导致包路径和类名不稳定。

## 🛠️ 解决方案

### 方案一：使用最新 SNAPSHOT 版本（已配置）

已经将 Spring AI 版本更新为 `1.0.0-SNAPSHOT`，这是最新的开发版本。

**尝试构建**：
```bash
docker-compose build ai-robot
```

### 方案二：暂时禁用 AI 功能（快速启动）

如果你想先让服务启动起来，可以暂时注释掉 AI 功能：

1. 修改 `AiChatService.java`，在 chat 方法中返回模拟数据：

```java
public ChatResponse chat(String userId, ChatRequest request) {
    // 临时模拟响应，等Spring AI稳定后再启用
    return ChatResponse.builder()
        .conversationId("mock-conversation-id")
        .messageId("mock-message-id")
        .content("AI服务正在维护中，这是一个模拟响应")
        .modelName("mock")
        .responseTime(0L)
        .build();
}
```

2. 注释掉 pom.xml 中的 Spring AI 依赖：

```xml
<!-- 暂时禁用Spring AI -->
<!--
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>${spring-ai.version}</version>
</dependency>
-->
```

### 方案三：使用稳定的 OpenAI Java SDK

使用官方 OpenAI Java SDK 替代 Spring AI：

1. 修改 `pom.xml`，添加依赖：

```xml
<!-- OpenAI Java SDK -->
<dependency>
    <groupId>com.theokanning.openai-gpt3-java</groupId>
    <artifactId>service</artifactId>
    <version>0.18.2</version>
</dependency>
```

2. 修改 `AiChatService.java`：

```java
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class AiChatService {
    private final OpenAiService openAiService;
    
    public AiChatService(@Value("${spring.ai.openai.api-key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }
    
    // 使用OpenAI SDK调用
}
```

## 📋 推荐操作步骤

### 1. 先让服务启动（使用方案二）

```bash
# 1. 注释掉 Spring AI 依赖
# 2. 修改 AiChatService 返回模拟数据
# 3. 重新构建
docker-compose build ai-robot

# 4. 启动服务
docker-compose up -d ai-robot

# 5. 查看日志
docker-compose logs -f ai-robot
```

### 2. 验证其他功能

先验证：
- 数据库连接是否正常
- 机器人管理接口是否可用
- 会话管理是否工作

```bash
# 获取公开机器人列表
curl http://localhost:8083/api/ai/robot/public

# 查看健康检查
curl http://localhost:8083/actuator/health
```

### 3. 等待 Spring AI 稳定后再集成

Spring AI 预计在 1.0.0 正式版会更稳定。届时再：
1. 更新到稳定版本
2. 根据官方文档更新 API 调用
3. 重新启用 AI 功能

## 🔍 调试技巧

### 查看 Docker 构建日志

```bash
# 查看详细构建过程
docker-compose build --no-cache --progress=plain ai-robot 2>&1 | tee build.log

# 查找错误
cat build.log | grep "ERROR"
```

### 查看运行日志

```bash
# 实时查看日志
docker-compose logs -f ai-robot

# 查看最近100行
docker-compose logs --tail=100 ai-robot
```

### 进入容器调试

```bash
# 如果容器启动了但有问题
docker exec -it my-framework-ai-robot sh

# 查看Java进程
ps aux | grep java

# 查看应用日志
cat /app/logs/framework-ai-robot.log
```

## 📚 相关资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [OpenAI Java SDK](https://github.com/TheoKanning/openai-java)
- [项目部署文档](./docs/DEPLOYMENT.md)

## 💬 需要帮助？

如果遇到其他问题：
1. 查看 `logs/framework-ai-robot.log` 日志文件
2. 检查 Docker 容器状态：`docker-compose ps`
3. 检查依赖服务是否正常（MySQL、Redis、Nacos）

## 🔄 临时建议

**当前最佳方案**：使用方案二（暂时禁用AI功能），先让服务启动起来，验证其他功能正常。等 Spring AI 1.0.0 正式版发布后，再集成完整的 AI 功能。

这样的好处：
1. 不影响其他服务的使用
2. 机器人管理、会话管理等功能可以正常开发和测试
3. 数据库结构已经准备好
4. 后续只需替换 AI 调用部分即可

---

**更新日期**: 2025-10-30
**状态**: 🔧 待修复 - Spring AI API 兼容性问题

