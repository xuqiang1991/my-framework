# 用户注册与管理指南

## 概述

本系统提供了完整的用户注册与管理解决方案，作为单点登录（SSO）的用户中心，支持以下三种用户注册方式：

1. **用户自助注册** - 通过前端页面自行注册账户
2. **第三方平台注册** - 第三方系统通过API直接注册用户
3. **管理员创建用户** - 管理员通过后台管理系统创建用户

## 功能特性

### ✨ 核心功能

- 🔐 **统一身份管理** - 所有用户统一存储在SSO用户中心
- 📝 **自助注册** - 用户可通过注册页面自行创建账户
- 🔑 **验证码保护** - 图形验证码防止机器注册
- 🔌 **第三方接入** - 提供安全的API供外部平台注册用户
- 👥 **管理员维护** - 完整的用户CRUD管理功能
- 🔗 **跨平台绑定** - 支持用户在多个平台间账号关联
- 📊 **数据统计** - 实时用户统计和活跃度分析

## 一、用户自助注册

### 1.1 访问注册页面

**页面地址**：`http://localhost:8082/self-register.html`

### 1.2 注册流程

1. 访问注册页面
2. 填写用户信息：
   - **用户名**（必填）：4-32位字母、数字或下划线
   - **密码**（必填）：至少8位，建议包含大小写与数字
   - **确认密码**（必填）：再次输入密码
   - **昵称**（可选）：展示给其他用户的名称
   - **邮箱**（可选）：用于找回密码与通知
   - **手机号**（可选）：用于安全验证
   - **验证码**（必填）：输入图形验证码
3. 点击"立即注册"按钮
4. 注册成功后，自动跳转到登录页面

### 1.3 注册接口

**接口地址**：`POST /user/register/self`

**请求示例**：

```json
{
  "username": "newuser",
  "password": "Password123",
  "nickname": "新用户",
  "email": "user@example.com",
  "phone": "13800138000",
  "captcha": "ABCD",
  "captchaKey": "captcha-key-uuid"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "1234567890",
    "username": "newuser",
    "nickname": "新用户",
    "email": "user@example.com",
    "phone": "13800138000",
    "status": 1,
    "createTime": "2025-10-30T10:30:00"
  }
}
```

### 1.4 验证码接口

**获取验证码**：`GET /captcha/generate`

**响应示例**：

```json
{
  "code": 200,
  "data": {
    "captchaKey": "captcha-123456",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
```

## 二、第三方平台注册

### 2.1 概述

第三方平台可以通过安全的API接口直接为用户创建账号，无需用户手动注册。适用于：
- 已有用户系统的平台接入SSO
- 批量导入用户
- 自动化用户创建流程

### 2.2 获取API凭证

管理员需要先为第三方平台生成API凭证：

**接口地址**：`POST /api/third-party/admin/generate-key?platformId={platformId}`

**响应示例**：

```
API Key: ak_test_main_app_001
API Secret: sk_test_main_app_secret_001

请妥善保管API Secret，它将不会再次显示！
```

### 2.3 签名算法

为确保API安全，所有请求都需要进行签名验证：

**签名生成步骤**：

1. 将以下参数按字母顺序排序：
   - `apiKey`
   - `timestamp`
   - `platformId`
   - `externalUserId`
   - `username`（可选）

2. 将参数值拼接成字符串
3. 在末尾追加 `apiSecret`
4. 对整个字符串计算 MD5 值

**Java示例**：

```java
import cn.hutool.crypto.SecureUtil;
import java.util.TreeMap;

public String calculateSignature(
    String apiKey, 
    String apiSecret, 
    long timestamp, 
    String platformId, 
    String externalUserId, 
    String username
) {
    TreeMap<String, String> params = new TreeMap<>();
    params.put("apiKey", apiKey);
    params.put("timestamp", String.valueOf(timestamp));
    params.put("platformId", platformId);
    params.put("externalUserId", externalUserId);
    if (username != null) {
        params.put("username", username);
    }
    
    StringBuilder signStr = new StringBuilder();
    for (String value : params.values()) {
        signStr.append(value);
    }
    signStr.append(apiSecret);
    
    return SecureUtil.md5(signStr.toString());
}
```

**JavaScript示例**：

```javascript
const crypto = require('crypto');

function calculateSignature(apiKey, apiSecret, timestamp, platformId, externalUserId, username) {
    const params = {
        apiKey,
        timestamp: timestamp.toString(),
        platformId,
        externalUserId
    };
    
    if (username) {
        params.username = username;
    }
    
    // 按字母顺序排序
    const sortedKeys = Object.keys(params).sort();
    let signStr = '';
    for (const key of sortedKeys) {
        signStr += params[key];
    }
    signStr += apiSecret;
    
    return crypto.createHash('md5').update(signStr).digest('hex');
}
```

### 2.4 注册接口

**接口地址**：`POST /api/third-party/register`

**请求头**：
```
Content-Type: application/json
```

**请求示例**：

```json
{
  "platformId": "1",
  "externalUserId": "ext_user_12345",
  "externalUsername": "platform_username",
  "username": "newuser",
  "nickname": "平台用户",
  "realName": "张三",
  "email": "user@example.com",
  "phone": "13800138000",
  "gender": 1,
  "apiKey": "ak_test_main_app_001",
  "timestamp": 1698654321000,
  "signature": "calculated_md5_signature"
}
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| platformId | String | 是 | 平台ID |
| externalUserId | String | 是 | 第三方平台的用户ID |
| externalUsername | String | 否 | 第三方平台的用户名 |
| username | String | 否 | SSO用户名（若为空会自动生成） |
| nickname | String | 否 | 昵称 |
| realName | String | 否 | 真实姓名 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| gender | Integer | 否 | 性别：0-未知，1-男，2-女 |
| apiKey | String | 是 | API Key |
| timestamp | Long | 是 | 当前时间戳（毫秒） |
| signature | String | 是 | 签名 |

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "1234567890",
    "username": "newuser",
    "nickname": "平台用户",
    "email": "user@example.com",
    "status": 1
  }
}
```

### 2.5 智能处理策略

系统对第三方注册有以下智能处理：

1. **重复检测**：如果该平台用户已绑定账号，直接返回已有用户信息
2. **用户名冲突**：如果用户名已被占用，自动生成唯一用户名
3. **密码处理**：如果不提供密码，自动生成随机密码（第三方用户通过SSO登录，无需密码）

### 2.6 查询用户绑定状态

**接口地址**：`GET /api/third-party/user/status`

**请求参数**：
- `platformId`：平台ID
- `externalUserId`：外部用户ID
- `apiKey`：API Key

**响应示例**：

```json
{
  "code": 200,
  "data": {
    "userId": "1234567890",
    "username": "newuser",
    "nickname": "平台用户",
    "status": 1
  }
}
```

### 2.7 安全建议

1. **API Secret保护**
   - API Secret必须妥善保管，不要泄露
   - 仅在服务端使用API Secret
   - 定期轮换API凭证

2. **时间戳验证**
   - 请求时间戳与服务器时间误差不能超过5分钟
   - 防止重放攻击

3. **HTTPS传输**
   - 生产环境必须使用HTTPS
   - 防止凭证在传输过程中被窃取

4. **速率限制**
   - 建议实施API调用频率限制
   - 防止恶意大量注册

## 三、管理员用户管理

### 3.1 访问管理后台

**页面地址**：`http://localhost:8082/admin-user-management.html`

### 3.2 功能列表

#### 3.2.1 用户列表

- **搜索功能**：支持按用户名、昵称、邮箱、手机号搜索
- **分页显示**：每页10条记录
- **状态显示**：显示用户启用/禁用状态
- **实时统计**：显示总用户数、活跃用户、今日注册

#### 3.2.2 新增用户

点击"新增用户"按钮，填写用户信息：

- 用户名（必填）
- 密码（必填）
- 昵称
- 真实姓名
- 邮箱
- 手机号
- 性别
- 状态（启用/禁用）

**接口地址**：`POST /user/register/admin`

**请求示例**：

```json
{
  "username": "employee001",
  "password": "Welcome123",
  "nickname": "员工001",
  "realName": "张三",
  "email": "employee@company.com",
  "phone": "13800138000",
  "gender": 1
}
```

#### 3.2.3 编辑用户

点击用户列表中的"编辑"按钮，可修改用户信息：

- 用户名不可修改
- 密码可选（留空则不修改）
- 其他信息均可修改

**接口地址**：`PUT /user/update`

**请求示例**：

```json
{
  "userId": "1234567890",
  "nickname": "新昵称",
  "email": "newemail@example.com",
  "status": 1
}
```

#### 3.2.4 删除用户

点击"删除"按钮，系统会进行逻辑删除（软删除）。

**接口地址**：`DELETE /user/delete/{userId}`

#### 3.2.5 启用/禁用用户

点击"启用"或"禁用"按钮，快速切换用户状态。

**接口地址**：`PUT /user/update`

**请求示例**：

```json
{
  "userId": "1234567890",
  "status": 0
}
```

### 3.3 管理接口列表

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询用户列表 | GET | `/user/list` | 支持分页和搜索 |
| 获取用户详情 | GET | `/user/getById/{userId}` | 根据ID获取用户 |
| 创建用户 | POST | `/user/register/admin` | 管理员创建用户 |
| 更新用户 | PUT | `/user/update` | 更新用户信息 |
| 删除用户 | DELETE | `/user/delete/{userId}` | 逻辑删除用户 |
| 检查用户名 | GET | `/user/register/check-username` | 检查用户名是否可用 |

## 四、数据库表结构

### 4.1 用户表（sys_user）

```sql
CREATE TABLE `sys_user` (
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 4.2 用户平台关联表（sys_user_platform）

```sql
CREATE TABLE `sys_user_platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `platform_id` varchar(64) NOT NULL COMMENT '平台ID',
  `platform_user_id` varchar(128) DEFAULT NULL COMMENT '平台用户ID',
  `platform_username` varchar(128) DEFAULT NULL COMMENT '平台用户名',
  `bind_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-解绑，1-已绑定',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_platform` (`user_id`, `platform_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户平台关联表';
```

### 4.3 API凭证表（sys_api_credential）

```sql
CREATE TABLE `sys_api_credential` (
  `credential_id` varchar(64) NOT NULL COMMENT '凭证ID',
  `platform_id` varchar(64) NOT NULL COMMENT '平台ID',
  `api_key` varchar(128) NOT NULL COMMENT 'API Key（公开）',
  `api_secret` varchar(128) NOT NULL COMMENT 'API Secret（私密）',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',
  `last_used_at` datetime DEFAULT NULL COMMENT '最后使用时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`credential_id`),
  UNIQUE KEY `uk_api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API凭证表';
```

## 五、集成OAuth2 SSO

所有注册的用户都可以通过OAuth2单点登录访问接入的平台。

### 5.1 登录流程

1. 用户访问第三方应用
2. 应用引导用户到SSO授权端点
3. 用户登录（使用注册的用户名和密码）
4. 用户确认授权
5. SSO生成授权码并重定向
6. 应用使用授权码换取访问令牌
7. 应用使用令牌获取用户信息
8. 登录成功

详细说明请参考：[SSO.md](SSO.md)

### 5.2 OAuth2接口

- **授权端点**：`GET /oauth2/authorize`
- **令牌端点**：`POST /oauth2/token`
- **用户信息端点**：`GET /oauth2/userinfo`

## 六、常见问题

### Q1: 用户名已存在怎么办？

**自助注册**：提示用户选择其他用户名。

**第三方注册**：系统会自动生成唯一用户名，格式为：`{原用户名}_{平台ID后4位}_{6位随机数字}`

### Q2: 如何重置用户密码？

管理员可以通过管理后台编辑用户，设置新密码。

### Q3: 第三方平台用户如何登录？

第三方平台用户通过OAuth2单点登录流程访问。如果平台用户已绑定SSO账号，直接使用绑定的账号登录。

### Q4: API签名验证失败怎么办？

1. 检查API Key和Secret是否正确
2. 检查时间戳是否在有效范围内（±5分钟）
3. 检查签名算法是否正确
4. 确认参数拼接顺序（按字母顺序）

### Q5: 如何批量导入用户？

可以编写脚本调用第三方注册API批量导入，或者直接在数据库中插入用户记录。

## 七、最佳实践

### 7.1 用户注册

1. **验证码保护**：始终启用验证码防止机器注册
2. **邮箱验证**：建议增加邮箱验证流程
3. **密码强度**：要求至少8位，包含大小写字母和数字
4. **用户协议**：添加用户协议和隐私政策

### 7.2 第三方接入

1. **凭证管理**：定期轮换API凭证
2. **错误处理**：实现完善的错误处理和重试机制
3. **日志记录**：记录所有API调用日志
4. **监控告警**：监控API调用频率和失败率

### 7.3 用户管理

1. **权限控制**：仅授权管理员访问管理后台
2. **操作审计**：记录所有用户管理操作
3. **定期清理**：定期清理长期未使用的账号
4. **数据备份**：定期备份用户数据

## 八、相关文档

- [SSO 单点登录指南](SSO.md)
- [API 接口文档](API.md)
- [部署文档](DEPLOY.md)
- [系统架构说明](README.md)

## 九、技术支持

如有问题或建议，请提交Issue或联系技术支持团队。

---

**最后更新**：2025-10-30

