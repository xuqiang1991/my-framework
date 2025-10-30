# 单点登录（SSO）使用指南

## 概述

本系统基于 OAuth2 协议实现了单点登录（SSO）功能，支持多个平台通过统一的认证中心进行用户认证和授权。

## 核心特性

### 1. 多平台支持
- 支持多个应用平台接入统一认证中心
- 每个平台可以独立配置 OAuth2 客户端
- 支持 Web 应用和移动应用

### 2. 用户账号关联
- 用户可以在多个平台间关联账号
- 同一用户可以绑定多个平台
- 用户数据隔离但又可以关联
- 支持账号绑定和解绑操作

### 3. OAuth2 授权模式
- **授权码模式（Authorization Code）**：最安全的授权方式，适用于服务端应用
- **刷新令牌（Refresh Token）**：支持令牌刷新，延长用户会话

### 4. 安全特性
- 客户端密钥加密存储
- 授权码有效期控制（5分钟）
- 访问令牌和刷新令牌分离
- 支持令牌撤销
- CSRF 防护（state 参数）

## 数据库表结构

### 平台表（sys_platform）
存储接入 SSO 的各个平台信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| platform_id | varchar(64) | 平台ID |
| platform_code | varchar(64) | 平台编码 |
| platform_name | varchar(128) | 平台名称 |
| platform_url | varchar(512) | 平台地址 |
| status | tinyint(1) | 状态：0-禁用，1-启用 |

### 用户平台关联表（sys_user_platform）
存储用户与平台的绑定关系，实现账号关联。

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | varchar(64) | 用户ID |
| platform_id | varchar(64) | 平台ID |
| platform_user_id | varchar(128) | 平台用户ID |
| platform_username | varchar(128) | 平台用户名 |
| status | tinyint(1) | 状态：0-解绑，1-已绑定 |

### OAuth2客户端表（sys_oauth_client）
存储 OAuth2 客户端配置信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| client_id | varchar(64) | 客户端ID |
| client_secret | varchar(256) | 客户端密钥（加密） |
| redirect_uris | varchar(1024) | 重定向URI列表 |
| access_token_validity | int(11) | 访问令牌有效期（秒） |
| refresh_token_validity | int(11) | 刷新令牌有效期（秒） |
| auto_approve | tinyint(1) | 自动授权 |

### OAuth2授权码表（sys_oauth_code）
存储临时授权码。

| 字段 | 类型 | 说明 |
|------|------|------|
| code | varchar(128) | 授权码 |
| client_id | varchar(64) | 客户端ID |
| user_id | varchar(64) | 用户ID |
| expires_at | datetime | 过期时间 |
| used | tinyint(1) | 是否已使用 |

### OAuth2令牌表（sys_oauth_token）
存储访问令牌和刷新令牌。

| 字段 | 类型 | 说明 |
|------|------|------|
| access_token | varchar(256) | 访问令牌 |
| refresh_token | varchar(256) | 刷新令牌 |
| client_id | varchar(64) | 客户端ID |
| user_id | varchar(64) | 用户ID |
| access_token_expires_at | datetime | 访问令牌过期时间 |
| revoked | tinyint(1) | 是否已撤销 |

## OAuth2 授权流程

### 授权码模式流程图

```
┌─────────┐                                           ┌─────────┐
│  用户   │                                           │  客户端 │
└────┬────┘                                           └────┬────┘
     │                                                      │
     │  1. 访问客户端应用                                   │
     │ ─────────────────────────────────────────────────> │
     │                                                      │
     │  2. 重定向到 SSO 授权端点                           │
     │ <───────────────────────────────────────────────── │
     │    /oauth2/authorize?client_id=xxx&redirect_uri=xxx │
     │                                                      │
     ▼                                                      │
┌─────────┐                                                │
│ SSO服务 │                                                │
└────┬────┘                                                │
     │                                                      │
     │  3. 检查用户登录状态                                 │
     │      未登录：跳转到登录页面                          │
     │      已登录：检查是否需要用户授权                     │
     │                                                      │
     │  4. 用户登录/授权确认                                │
     │                                                      │
     │  5. 生成授权码并重定向                               │
     │ ─────────────────────────────────────────────────> │
     │    redirect_uri?code=xxx&state=xxx                  │
     │                                                      │
     │                                                      │
     │  6. 使用授权码换取访问令牌                           │
     │ <───────────────────────────────────────────────── │
     │    POST /oauth2/token                               │
     │    {                                                 │
     │      "grant_type": "authorization_code",            │
     │      "code": "xxx",                                 │
     │      "client_id": "xxx",                            │
     │      "client_secret": "xxx",                        │
     │      "redirect_uri": "xxx"                          │
     │    }                                                 │
     │                                                      │
     │  7. 返回访问令牌                                     │
     │ ─────────────────────────────────────────────────> │
     │    {                                                 │
     │      "access_token": "xxx",                         │
     │      "refresh_token": "xxx",                        │
     │      "token_type": "Bearer",                        │
     │      "expires_in": 3600                             │
     │    }                                                 │
     │                                                      │
     │  8. 使用访问令牌获取用户信息                         │
     │ <───────────────────────────────────────────────── │
     │    GET /oauth2/userinfo                             │
     │    Authorization: Bearer xxx                        │
     │                                                      │
     │  9. 返回用户信息                                     │
     │ ─────────────────────────────────────────────────> │
```

## API 接口说明

### 1. OAuth2 授权端点

**接口地址**：`GET /oauth2/authorize`

**请求参数**：
- `response_type`: 固定值 `code`
- `client_id`: 客户端ID（必填）
- `redirect_uri`: 重定向URI（必填）
- `scope`: 授权范围（可选）
- `state`: 状态码，用于防止CSRF攻击（建议填写）

**示例**：
```
GET /oauth2/authorize?response_type=code&client_id=main-app-client&redirect_uri=http://localhost:8080/callback&scope=read,write&state=xyz123
```

**响应**：
- 如果用户未登录：重定向到 SSO 登录页面
- 如果用户已登录且客户端设置了自动授权：直接重定向到 `redirect_uri?code=xxx&state=xxx`
- 如果用户已登录但需要授权确认：重定向到授权确认页面

### 2. OAuth2 令牌端点

**接口地址**：`POST /oauth2/token`

**使用授权码换取令牌**：

```json
{
  "grant_type": "authorization_code",
  "code": "授权码",
  "client_id": "客户端ID",
  "client_secret": "客户端密钥",
  "redirect_uri": "重定向URI"
}
```

**使用刷新令牌获取新令牌**：

```json
{
  "grant_type": "refresh_token",
  "refresh_token": "刷新令牌",
  "client_id": "客户端ID",
  "client_secret": "客户端密钥"
}
```

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "access_token": "abc123...",
    "refresh_token": "def456...",
    "token_type": "Bearer",
    "expires_in": 3600,
    "scope": "read,write"
  }
}
```

### 3. 获取用户信息

**接口地址**：`GET /oauth2/userinfo`

**请求头**：
```
Authorization: Bearer {access_token}
```

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "userId": "1",
    "username": "admin",
    "nickname": "管理员"
  }
}
```

### 4. 验证令牌

**接口地址**：`POST /oauth2/introspect`

**请求参数**：
- `token`: 访问令牌

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "userId": "1",
    "clientId": "main-app-client",
    "scope": "read,write",
    "expiresAt": "2025-10-29T12:00:00"
  }
}
```

### 5. 撤销令牌

**接口地址**：`POST /oauth2/revoke`

**请求参数**：
- `token`: 访问令牌

### 6. SSO 登录

**接口地址**：`POST /auth/sso/login`

**请求参数**：
```json
{
  "username": "admin",
  "password": "123456"
}
```

**URL 参数**：
- `client_id`: 客户端ID
- `redirect_uri`: 重定向URI
- `scope`: 授权范围（可选）
- `state`: 状态码（可选）

**响应示例**：
```json
{
  "code": 200,
  "data": {
    "code": "授权码",
    "redirectUrl": "http://localhost:8080/callback?code=xxx&state=xxx",
    "userInfo": {
      "userId": "1",
      "username": "admin",
      "nickname": "管理员"
    }
  }
}
```

### 7. 用户平台账号管理

#### 7.1 绑定平台账号

**接口地址**：`POST /auth/platform/bind`

**请求体**：
```json
{
  "platformId": "2",
  "platformUserId": "user123",
  "platformUsername": "测试用户"
}
```

#### 7.2 解绑平台账号

**接口地址**：`POST /auth/platform/unbind`

**请求参数**：
- `platformId`: 平台ID

#### 7.3 获取已绑定平台列表

**接口地址**：`GET /auth/platform/bound`

**响应示例**：
```json
{
  "code": 200,
  "data": [
    {
      "platformId": "1",
      "platformCode": "MAIN_APP",
      "platformName": "主应用平台",
      "platformUrl": "http://localhost:8080"
    }
  ]
}
```

## 客户端接入指南

### 步骤 1：注册 OAuth2 客户端

在数据库中注册客户端信息（或通过管理界面）：

```sql
INSERT INTO sys_oauth_client (
  client_id, 
  client_secret, 
  client_name, 
  platform_id, 
  redirect_uris, 
  grant_types, 
  scope, 
  access_token_validity,
  refresh_token_validity,
  auto_approve
) VALUES (
  'my-app-client',
  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', -- 密文：secret123
  '我的应用',
  '1',
  'http://localhost:8080/callback,http://localhost:8080/login/callback',
  'authorization_code,refresh_token',
  'read,write,user_info',
  3600,
  604800,
  1
);
```

### 步骤 2：实现授权流程

#### 2.1 引导用户到授权页面

```javascript
// 生成随机 state 用于防 CSRF
const state = generateRandomString();
sessionStorage.setItem('oauth_state', state);

// 构建授权 URL
const authUrl = 'http://localhost:9001/oauth2/authorize?' + 
  'response_type=code' +
  '&client_id=my-app-client' +
  '&redirect_uri=' + encodeURIComponent('http://localhost:8080/callback') +
  '&scope=read,write,user_info' +
  '&state=' + state;

// 重定向到授权页面
window.location.href = authUrl;
```

#### 2.2 处理回调并获取令牌

```javascript
// 在回调页面 /callback
const urlParams = new URLSearchParams(window.location.search);
const code = urlParams.get('code');
const state = urlParams.get('state');

// 验证 state
if (state !== sessionStorage.getItem('oauth_state')) {
  console.error('State mismatch - possible CSRF attack');
  return;
}

// 使用授权码换取访问令牌
fetch('http://localhost:9001/oauth2/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    grant_type: 'authorization_code',
    code: code,
    client_id: 'my-app-client',
    client_secret: 'secret123',
    redirect_uri: 'http://localhost:8080/callback'
  })
})
.then(response => response.json())
.then(data => {
  // 保存令牌
  localStorage.setItem('access_token', data.data.access_token);
  localStorage.setItem('refresh_token', data.data.refresh_token);
  
  // 跳转到应用首页
  window.location.href = '/';
});
```

#### 2.3 使用访问令牌调用 API

```javascript
// 在后续的 API 请求中携带访问令牌
fetch('http://localhost:9001/oauth2/userinfo', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('access_token')
  }
})
.then(response => response.json())
.then(data => {
  console.log('用户信息:', data.data);
});
```

#### 2.4 刷新令牌

```javascript
// 当访问令牌过期时，使用刷新令牌获取新的访问令牌
fetch('http://localhost:9001/oauth2/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    grant_type: 'refresh_token',
    refresh_token: localStorage.getItem('refresh_token'),
    client_id: 'my-app-client',
    client_secret: 'secret123'
  })
})
.then(response => response.json())
.then(data => {
  // 更新令牌
  localStorage.setItem('access_token', data.data.access_token);
  localStorage.setItem('refresh_token', data.data.refresh_token);
});
```

### 步骤 3：Java 客户端示例

```java
// 引导用户到授权页面
@GetMapping("/login")
public String login() {
    String state = UUID.randomUUID().toString();
    // 保存 state 到 session
    session.setAttribute("oauth_state", state);
    
    String authUrl = "http://localhost:9001/oauth2/authorize" +
        "?response_type=code" +
        "&client_id=my-app-client" +
        "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/callback", "UTF-8") +
        "&scope=read,write,user_info" +
        "&state=" + state;
    
    return "redirect:" + authUrl;
}

// 处理回调
@GetMapping("/callback")
public String callback(@RequestParam String code, @RequestParam String state) {
    // 验证 state
    String savedState = (String) session.getAttribute("oauth_state");
    if (!state.equals(savedState)) {
        throw new SecurityException("State mismatch");
    }
    
    // 使用授权码换取令牌
    RestTemplate restTemplate = new RestTemplate();
    Map<String, String> request = new HashMap<>();
    request.put("grant_type", "authorization_code");
    request.put("code", code);
    request.put("client_id", "my-app-client");
    request.put("client_secret", "secret123");
    request.put("redirect_uri", "http://localhost:8080/callback");
    
    ResponseEntity<OAuth2TokenResponse> response = restTemplate.postForEntity(
        "http://localhost:9001/oauth2/token",
        request,
        OAuth2TokenResponse.class
    );
    
    OAuth2TokenResponse tokenResponse = response.getBody();
    
    // 保存令牌到 session
    session.setAttribute("access_token", tokenResponse.getAccessToken());
    session.setAttribute("refresh_token", tokenResponse.getRefreshToken());
    
    return "redirect:/";
}
```

## 用户账号关联场景

### 场景 1：新用户在多个平台注册

1. 用户在平台 A 注册并登录
2. 用户访问平台 B，通过 SSO 登录
3. 系统检测到用户在平台 A 已有账号
4. 自动创建平台 B 的关联关系

### 场景 2：已有用户绑定新平台

1. 用户在主应用登录
2. 访问"账号管理"页面
3. 选择"绑定新平台"
4. 输入新平台的用户信息
5. 系统创建关联关系

### 场景 3：跨平台单点登录

1. 用户在平台 A 登录
2. 用户访问平台 B
3. 平台 B 检测到用户未登录
4. 重定向到 SSO 授权端点
5. SSO 检测到用户在平台 A 已登录
6. 自动生成授权码并重定向
7. 平台 B 获取令牌，用户无需再次登录

## 安全建议

1. **客户端密钥保护**
   - 客户端密钥必须妥善保管，不要泄露
   - 仅在服务端使用客户端密钥
   - 定期轮换客户端密钥

2. **使用 HTTPS**
   - 生产环境必须使用 HTTPS
   - 防止令牌在传输过程中被窃取

3. **state 参数**
   - 始终使用 state 参数防止 CSRF 攻击
   - state 应该是随机生成的不可预测值

4. **令牌管理**
   - 访问令牌有效期不宜过长（建议 1-2 小时）
   - 刷新令牌有效期可以较长（7-30 天）
   - 用户登出时撤销所有令牌

5. **重定向 URI 验证**
   - 严格验证重定向 URI
   - 不允许动态注册重定向 URI
   - 使用精确匹配而非模式匹配

## 测试客户端信息

系统已预置三个测试客户端：

### 1. 主应用客户端
- **Client ID**: `main-app-client`
- **Client Secret**: `secret123`
- **Redirect URIs**: 
  - `http://localhost:8080/callback`
  - `http://localhost:8080/login/callback`
- **Auto Approve**: 是

### 2. 管理后台客户端
- **Client ID**: `admin-client`
- **Client Secret**: `secret123`
- **Redirect URIs**: `http://localhost:8081/callback`
- **Auto Approve**: 否（需要用户授权确认）

### 3. 移动端客户端
- **Client ID**: `mobile-client`
- **Client Secret**: `secret123`
- **Redirect URIs**: `myapp://callback`
- **Auto Approve**: 是

## 常见问题

### Q1: 授权码已使用或无效
**原因**：授权码只能使用一次，且有效期为 5 分钟。

**解决**：重新发起授权请求获取新的授权码。

### Q2: 重定向 URI 不匹配
**原因**：回调时使用的 redirect_uri 与授权时不一致。

**解决**：确保两次请求使用完全相同的 redirect_uri。

### Q3: 访问令牌已过期
**原因**：访问令牌默认有效期为 1 小时。

**解决**：使用刷新令牌获取新的访问令牌。

### Q4: 如何实现单点登出（SLO）
**方案**：
1. 客户端调用 `/auth/sso/logout` 接口
2. SSO 服务清除用户会话
3. SSO 服务通知所有已登录的客户端
4. 客户端清除本地令牌

## 相关文档

- [API 文档](API.md)
- [部署文档](DEPLOY.md)
- [开发指南](CONTRIBUTING.md)

## 技术支持

如有问题，请查阅项目文档或联系技术支持团队。

