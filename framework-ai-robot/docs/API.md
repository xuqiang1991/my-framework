# AI机器人服务 API 文档

## 📝 接口说明

本文档详细说明AI机器人服务的所有REST API接口。

**基础信息**：
- **Base URL**: `http://localhost:8083`
- **API文档**: `http://localhost:8083/doc.html` (Knife4j)
- **认证方式**: Bearer Token (Sa-Token)
- **Content-Type**: `application/json`

## 🔐 认证说明

所有需要认证的接口都需要在请求头中携带Token：

```http
Authorization: Bearer {your-token-here}
```

获取Token的方式：
1. 通过认证服务登录：`POST http://localhost:8080/api/auth/login`
2. 获取返回的token
3. 在后续请求中使用该token

## 📡 接口列表

### 1. AI聊天接口

#### 1.1 发送聊天消息

发送消息给AI机器人并获取回复。

**接口地址**: `POST /api/ai/chat/send`

**请求头**:
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
```json
{
  "conversationId": "会话ID（可选，不传则创建新会话）",
  "robotId": "1",
  "message": "你好，请介绍一下你自己",
  "stream": false,
  "temperature": 0.7,
  "maxTokens": 2000
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | String | 否 | 会话ID，不传则创建新会话 |
| robotId | String | 是 | 机器人ID |
| message | String | 是 | 用户消息内容 |
| stream | Boolean | 否 | 是否流式输出（暂不支持） |
| temperature | Double | 否 | 温度参数，覆盖机器人默认配置 |
| maxTokens | Integer | 否 | 最大Token数，覆盖机器人默认配置 |

**响应示例**:
```json
{
  "conversationId": "1719234567890123456",
  "messageId": "1719234567890123457",
  "content": "你好！我是通用助手，一个友好、专业的AI助手...",
  "tokenCount": 50,
  "modelName": "gpt-3.5-turbo",
  "responseTime": 2350
}
```

**响应字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| conversationId | String | 会话ID |
| messageId | String | 消息ID |
| content | String | AI回复内容 |
| tokenCount | Integer | Token使用量 |
| modelName | String | 使用的模型名称 |
| responseTime | Long | 响应时间（毫秒） |

**cURL示例**:
```bash
curl -X POST http://localhost:8083/api/ai/chat/send \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "robotId": "1",
    "message": "你好"
  }'
```

---

#### 1.2 获取会话详情

获取指定会话的详细信息，包括所有历史消息。

**接口地址**: `GET /api/ai/chat/conversation/{conversationId}`

**请求头**:
```http
Authorization: Bearer {token}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | String | 是 | 会话ID |

**响应示例**:
```json
{
  "conversationId": "1719234567890123456",
  "title": "新对话",
  "robot": {
    "robotId": "1",
    "robotName": "通用助手",
    "robotAvatar": "https://api.dicebear.com/7.x/bottts/svg?seed=general"
  },
  "messages": [
    {
      "messageId": "1719234567890123457",
      "role": "user",
      "content": "你好",
      "tokenCount": 2,
      "createTime": "2024-01-20 10:30:00"
    },
    {
      "messageId": "1719234567890123458",
      "role": "assistant",
      "content": "你好！我是通用助手...",
      "tokenCount": 50,
      "createTime": "2024-01-20 10:30:02"
    }
  ],
  "messageCount": 2,
  "createTime": "2024-01-20 10:30:00",
  "updateTime": "2024-01-20 10:30:02"
}
```

**cURL示例**:
```bash
curl -X GET http://localhost:8083/api/ai/chat/conversation/1719234567890123456 \
  -H "Authorization: Bearer your-token"
```

---

#### 1.3 获取会话列表

获取当前用户的所有会话列表。

**接口地址**: `GET /api/ai/chat/conversations`

**请求头**:
```http
Authorization: Bearer {token}
```

**响应示例**:
```json
[
  {
    "conversationId": "1719234567890123456",
    "userId": "1",
    "robotId": "1",
    "title": "关于Spring Boot的讨论",
    "status": 1,
    "messageCount": 10,
    "createTime": "2024-01-20 10:30:00",
    "updateTime": "2024-01-20 11:00:00"
  },
  {
    "conversationId": "1719234567890123789",
    "userId": "1",
    "robotId": "2",
    "title": "代码优化建议",
    "status": 1,
    "messageCount": 5,
    "createTime": "2024-01-19 15:20:00",
    "updateTime": "2024-01-19 15:45:00"
  }
]
```

**cURL示例**:
```bash
curl -X GET http://localhost:8083/api/ai/chat/conversations \
  -H "Authorization: Bearer your-token"
```

---

#### 1.4 删除会话

删除指定的会话及其所有消息（逻辑删除）。

**接口地址**: `DELETE /api/ai/chat/conversation/{conversationId}`

**请求头**:
```http
Authorization: Bearer {token}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | String | 是 | 会话ID |

**响应**: 无内容（HTTP 200）

**cURL示例**:
```bash
curl -X DELETE http://localhost:8083/api/ai/chat/conversation/1719234567890123456 \
  -H "Authorization: Bearer your-token"
```

---

### 2. 机器人管理接口

#### 2.1 创建机器人

创建一个新的AI机器人配置。

**接口地址**: `POST /api/ai/robot`

**请求头**:
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
```json
{
  "robotName": "我的专属助手",
  "robotDesc": "一个专门帮我处理工作的AI助手",
  "robotAvatar": "https://example.com/avatar.png",
  "modelName": "gpt-3.5-turbo",
  "systemPrompt": "你是一个专业的工作助手...",
  "temperature": 0.7,
  "maxTokens": 2000,
  "isPublic": 0,
  "remark": "个人使用"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| robotName | String | 是 | 机器人名称 |
| robotDesc | String | 否 | 机器人描述 |
| robotAvatar | String | 否 | 机器人头像URL |
| modelName | String | 是 | 模型名称（如gpt-3.5-turbo） |
| systemPrompt | String | 否 | 系统提示词 |
| temperature | Double | 否 | 温度参数（0.0-2.0） |
| maxTokens | Integer | 否 | 最大Token数 |
| isPublic | Integer | 否 | 是否公开（0-私有，1-公开） |
| remark | String | 否 | 备注 |

**响应示例**:
```json
{
  "robotId": "1719234567890123456",
  "robotName": "我的专属助手",
  "robotDesc": "一个专门帮我处理工作的AI助手",
  "robotAvatar": "https://example.com/avatar.png",
  "modelName": "gpt-3.5-turbo",
  "systemPrompt": "你是一个专业的工作助手...",
  "temperature": 0.7,
  "maxTokens": 2000,
  "status": 1,
  "isPublic": 0,
  "createBy": "1",
  "createTime": "2024-01-20 10:30:00",
  "updateTime": "2024-01-20 10:30:00"
}
```

**cURL示例**:
```bash
curl -X POST http://localhost:8083/api/ai/robot \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "robotName": "我的助手",
    "modelName": "gpt-3.5-turbo",
    "temperature": 0.7
  }'
```

---

#### 2.2 更新机器人

更新机器人配置信息。

**接口地址**: `PUT /api/ai/robot`

**请求头**:
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
```json
{
  "robotId": "1719234567890123456",
  "robotName": "更新后的名称",
  "robotDesc": "更新后的描述",
  "temperature": 0.8
}
```

**响应**: 无内容（HTTP 200）

**cURL示例**:
```bash
curl -X PUT http://localhost:8083/api/ai/robot \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "robotId": "1719234567890123456",
    "robotName": "新名称"
  }'
```

---

#### 2.3 删除机器人

删除指定的机器人配置（逻辑删除）。

**接口地址**: `DELETE /api/ai/robot/{robotId}`

**请求头**:
```http
Authorization: Bearer {token}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| robotId | String | 是 | 机器人ID |

**响应**: 无内容（HTTP 200）

**cURL示例**:
```bash
curl -X DELETE http://localhost:8083/api/ai/robot/1719234567890123456 \
  -H "Authorization: Bearer your-token"
```

---

#### 2.4 获取机器人详情

获取指定机器人的详细信息。

**接口地址**: `GET /api/ai/robot/{robotId}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| robotId | String | 是 | 机器人ID |

**响应示例**:
```json
{
  "robotId": "1",
  "robotName": "通用助手",
  "robotDesc": "一个友好、专业的AI助手",
  "robotAvatar": "https://api.dicebear.com/7.x/bottts/svg?seed=general",
  "modelName": "gpt-3.5-turbo",
  "systemPrompt": "你是一个友好、专业的AI助手...",
  "temperature": 0.7,
  "maxTokens": 2000,
  "status": 1,
  "isPublic": 1,
  "createTime": "2024-01-01 00:00:00"
}
```

**cURL示例**:
```bash
curl -X GET http://localhost:8083/api/ai/robot/1
```

---

#### 2.5 获取公开机器人列表

获取所有公开的机器人列表（无需认证）。

**接口地址**: `GET /api/ai/robot/public`

**响应示例**:
```json
[
  {
    "robotId": "1",
    "robotName": "通用助手",
    "robotDesc": "一个友好、专业的AI助手",
    "robotAvatar": "https://api.dicebear.com/7.x/bottts/svg?seed=general",
    "isPublic": 1,
    "status": 1
  },
  {
    "robotId": "2",
    "robotName": "代码助手",
    "robotDesc": "专注于编程和技术问题的AI助手",
    "robotAvatar": "https://api.dicebear.com/7.x/bottts/svg?seed=code",
    "isPublic": 1,
    "status": 1
  }
]
```

**cURL示例**:
```bash
curl -X GET http://localhost:8083/api/ai/robot/public
```

---

#### 2.6 获取我的机器人列表

获取当前用户创建的所有机器人。

**接口地址**: `GET /api/ai/robot/my`

**请求头**:
```http
Authorization: Bearer {token}
```

**响应示例**: 同2.5

**cURL示例**:
```bash
curl -X GET http://localhost:8083/api/ai/robot/my \
  -H "Authorization: Bearer your-token"
```

---

#### 2.7 分页查询机器人

分页查询机器人列表，支持关键词搜索。

**接口地址**: `GET /api/ai/robot/page`

**查询参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |

**响应示例**:
```json
{
  "records": [
    {
      "robotId": "1",
      "robotName": "通用助手",
      "robotDesc": "一个友好、专业的AI助手",
      "status": 1,
      "isPublic": 1
    }
  ],
  "total": 10,
  "size": 10,
  "current": 1,
  "pages": 1
}
```

**cURL示例**:
```bash
curl -X GET "http://localhost:8083/api/ai/robot/page?pageNum=1&pageSize=10&keyword=助手"
```

---

## 🔄 错误码说明

| HTTP状态码 | 说明 |
|------------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**错误响应示例**:
```json
{
  "code": 400,
  "message": "机器人不存在或已禁用",
  "timestamp": "2024-01-20T10:30:00"
}
```

## 📚 使用示例

### JavaScript/TypeScript

```typescript
// 发送聊天消息
const sendMessage = async (message: string) => {
  const response = await fetch('http://localhost:8083/api/ai/chat/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      robotId: '1',
      message: message
    })
  });
  
  return await response.json();
};
```

### Python

```python
import requests

def send_message(token, message):
    url = 'http://localhost:8083/api/ai/chat/send'
    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {token}'
    }
    data = {
        'robotId': '1',
        'message': message
    }
    
    response = requests.post(url, json=data, headers=headers)
    return response.json()
```

### Java

```java
// 使用Spring RestTemplate
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(token);

ChatRequest request = new ChatRequest();
request.setRobotId("1");
request.setMessage("你好");

HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
ChatResponse response = restTemplate.postForObject(
    "http://localhost:8083/api/ai/chat/send",
    entity,
    ChatResponse.class
);
```

## 🔗 相关链接

- [在线API文档](http://localhost:8083/doc.html)
- [部署指南](./DEPLOYMENT.md)
- [项目README](../README.md)

