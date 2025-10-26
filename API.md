# API接口文档

## 基础说明

### 请求格式
- Content-Type: `application/json`
- 字符编码: `UTF-8`

### 响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1698051234567
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | Token已过期 |
| 1002 | Token无效 |
| 1003 | 用户不存在 |
| 1004 | 密码错误 |
| 1005 | 用户已被禁用 |
| 1006 | 用户已存在 |

## 认证服务 API

Base URL: `http://localhost:8080/auth`

### 1. 用户登录

**接口地址**: `/login`  
**请求方式**: `POST`  
**是否需要认证**: 否

#### 请求参数

```json
{
  "username": "admin",
  "password": "123456",
  "captcha": "1234",
  "captchaKey": "xxx-xxx-xxx",
  "rememberMe": false
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| captcha | String | 否 | 验证码 |
| captchaKey | String | 否 | 验证码Key |
| rememberMe | Boolean | 否 | 记住我 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "userId": "1",
    "username": "admin",
    "nickname": "管理员",
    "roles": ["ADMIN"],
    "permissions": []
  },
  "timestamp": 1698051234567
}
```

### 2. 用户登出

**接口地址**: `/logout`  
**请求方式**: `POST`  
**是否需要认证**: 是

#### 请求头
```
Authorization: Bearer {token}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1698051234567
}
```

### 3. 刷新Token

**接口地址**: `/refresh`  
**请求方式**: `POST`  
**是否需要认证**: 否

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 刷新Token |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "eyJhbGciOiJIUzI1NiJ9...",
  "timestamp": 1698051234567
}
```

### 4. 验证Token

**接口地址**: `/validate`  
**请求方式**: `POST`  
**是否需要认证**: 否

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | 访问Token |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true,
  "timestamp": 1698051234567
}
```

### 5. 获取验证码

**接口地址**: `/captcha`  
**请求方式**: `GET`  
**是否需要认证**: 否

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "xxx-xxx-xxx",
  "timestamp": 1698051234567
}
```

### 6. 获取当前用户信息

**接口地址**: `/info`  
**请求方式**: `GET`  
**是否需要认证**: 是

#### 请求头
```
Authorization: Bearer {token}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "1",
    "username": "admin",
    "nickname": "管理员",
    "roles": ["ADMIN"],
    "permissions": []
  },
  "timestamp": 1698051234567
}
```

## 用户服务 API

Base URL: `http://localhost:8080/user`

### 1. 根据用户名获取用户

**接口地址**: `/getByUsername/{username}`  
**请求方式**: `GET`  
**是否需要认证**: 是

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "1",
    "username": "admin",
    "nickname": "管理员",
    "realName": "管理员",
    "phone": null,
    "email": null,
    "gender": 0,
    "avatar": null,
    "status": 1,
    "roles": ["USER"],
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  },
  "timestamp": 1698051234567
}
```

### 2. 根据用户ID获取用户

**接口地址**: `/getById/{userId}`  
**请求方式**: `GET`  
**是否需要认证**: 是

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |

#### 响应示例

同上

### 3. 创建用户

**接口地址**: `/create`  
**请求方式**: `POST`  
**是否需要认证**: 是

#### 请求参数

```json
{
  "username": "test",
  "password": "123456",
  "nickname": "测试用户",
  "realName": "测试",
  "phone": "13800138000",
  "email": "test@example.com",
  "gender": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| nickname | String | 否 | 昵称 |
| realName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| gender | Integer | 否 | 性别：0-未知，1-男，2-女 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "2",
    "username": "test",
    "nickname": "测试用户",
    ...
  },
  "timestamp": 1698051234567
}
```

### 4. 更新用户

**接口地址**: `/update`  
**请求方式**: `PUT`  
**是否需要认证**: 是

#### 请求参数

```json
{
  "userId": "2",
  "nickname": "新昵称",
  "realName": "新姓名",
  "phone": "13900139000",
  "email": "new@example.com",
  "gender": 2
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "2",
    "username": "test",
    "nickname": "新昵称",
    ...
  },
  "timestamp": 1698051234567
}
```

### 5. 删除用户

**接口地址**: `/delete/{userId}`  
**请求方式**: `DELETE`  
**是否需要认证**: 是

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1698051234567
}
```

## Postman测试集

可以导入以下Postman Collection进行测试：

```json
{
  "info": {
    "name": "My Framework API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "认证服务",
      "item": [
        {
          "name": "用户登录",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"username\":\"admin\",\"password\":\"123456\"}"
            },
            "url": "http://localhost:8080/auth/login"
          }
        }
      ]
    }
  ]
}
```

