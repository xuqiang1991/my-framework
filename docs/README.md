# My Framework - Spring Cloud Alibaba 微服务框架

## 📝 项目简介

这是一个基于 **Spring Cloud Alibaba** 构建的现代化微服务框架，集成了主流的微服务技术栈，提供了完整的微服务解决方案。

### ✨ 核心特性

- 🔐 **统一鉴权**: Sa-Token + JWT + OAuth2 单点登录（SSO）
- 👥 **多平台支持**: 支持多应用接入、用户账号关联、跨平台单点登录
- 🚪 **网关路由**: Spring Cloud Gateway + 动态路由
- 📡 **服务治理**: Nacos服务注册与发现、配置中心
- 💾 **数据存储**: MySQL 8.0 + MyBatis Plus + Druid
- 🔥 **缓存方案**: Redis + 分布式缓存
- 📊 **监控体系**: Sentinel + Prometheus + Grafana + **SkyWalking**
- 🔍 **分布式追踪**: SkyWalking APM 全链路监控
- 🐳 **容器化**: Docker + Docker Compose 一键部署
- ☕ **最新技术**: JDK 21 + Spring Boot 3.2

## 🏗️ 架构设计

```
my-framework
├── framework-common       # 公共模块（工具类、常量、异常处理）
├── framework-api          # API模块（Feign接口定义）
├── framework-gateway      # 网关服务（路由、鉴权、限流）
├── framework-auth         # 认证服务（登录、Token管理）
└── framework-user         # 用户服务（用户管理示例）
```

### 架构图

```
                    ┌─────────────────┐
                    │   前端应用       │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Gateway网关    │
                    │  - 路由转发     │
                    │  - 统一鉴权     │
                    │  - 限流熔断     │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
  ┌───────▼───────┐  ┌──────▼──────┐  ┌───────▼───────┐
  │  Auth Service │  │ User Service │  │ Other Service │
  │  认证服务     │  │  用户服务    │  │  其他服务     │
  └───────┬───────┘  └──────┬──────┘  └───────┬───────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
      ┌────────────────────┬─┴──┬────────────────┐
      │                    │    │                │
┌─────▼─────┐       ┌─────▼────▼──┐      ┌─────▼─────┐
│   Nacos   │       │    MySQL    │      │   Redis   │
│服务注册/配置│      │   数据库     │      │   缓存    │
└───────────┘       └─────────────┘      └───────────┘
```

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | Java开发工具包 |
| Spring Boot | 3.2.0 | 基础框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | 阿里微服务套件 |
| Nacos | 2.3.0 | 服务注册与配置中心 |
| Sentinel | 1.8.6 | 流量控制、熔断降级 |
| Gateway | 4.1.0 | 微服务网关 |
| Sa-Token | 1.37.0 | 权限认证框架 |
| JWT | 0.12.3 | Token生成与验证 |
| MySQL | 8.0 | 关系型数据库 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| Redis | 7.x | 缓存数据库 |
| Druid | 1.2.20 | 数据库连接池 |
| Hutool | 5.8.23 | Java工具类库 |
| Knife4j | 4.4.0 | API文档工具 |
| Prometheus | latest | 监控系统 |
| Grafana | latest | 数据可视化 |
| **SkyWalking** | **9.7.0** | **分布式追踪APM** |
| Elasticsearch | 8.11.0 | SkyWalking 存储 |
| Docker | latest | 容器化平台 |

## 🚀 快速开始

### 前置要求

- JDK 21+
- Maven 3.8+
- Docker & Docker Compose
- Git

### 方式一：Docker Compose 部署（推荐）

1. **克隆项目**
```bash
git clone <your-repo-url>
cd my-framework
```

2. **下载 SkyWalking Agent（首次启动必需）**
```bash
# Linux/Mac
cd docker/skywalking
chmod +x download-agent.sh
./download-agent.sh
cd ../..

# Windows
cd docker\skywalking
download-agent.bat
cd ..\..
```

3. **构建项目**
```bash
# Linux/Mac
chmod +x scripts/dev/build.sh
./scripts/dev/build.sh

# Windows
scripts\dev\build.bat
```

4. **启动所有服务**
```bash
# Linux/Mac
chmod +x scripts/dev/start.sh
./scripts/dev/start.sh

# Windows
scripts\dev\start.bat
```

5. **等待服务启动完成（约3-5分钟）**

### 方式二：本地开发模式

1. **启动基础设施**
```bash
docker-compose up -d mysql redis nacos sentinel
```

2. **等待Nacos启动完成（约1分钟）**

3. **启动各个微服务**
```bash
# 启动网关服务
cd framework-gateway
mvn spring-boot:run

# 启动认证服务
cd framework-auth
mvn spring-boot:run

# 启动用户服务
cd framework-user
mvn spring-boot:run
```

### 停止服务

```bash
# Linux/Mac
chmod +x scripts/dev/stop.sh
./scripts/dev/stop.sh

# Windows
scripts\dev\stop.bat
```

## 📡 服务地址

| 服务 | 地址 | 账号/密码 | 说明 |
|------|------|-----------|------|
| 网关服务 | http://localhost:8080 | - | 统一入口 |
| 认证服务 | http://localhost:8081 | - | 认证服务直连 |
| 用户服务 | http://localhost:8082 | - | 用户服务直连 |
| Nacos控制台 | http://localhost:8848/nacos | nacos/nacos | 服务注册/配置 |
| Sentinel控制台 | http://localhost:8858 | sentinel/sentinel | 流控管理 |
| Prometheus | http://localhost:9090 | - | 监控数据 |
| Grafana | http://localhost:3000 | admin/admin | 数据可视化 |
| SkyWalking UI** | **http://localhost:8088 | 分布式追踪UI |
| Elasticsearch | http://localhost:9200 | - | SkyWalking 存储 |
| MySQL | localhost:3306 | root/root | 数据库 |
| Redis | localhost:6379 | - | 缓存 |
| Druid监控 | http://localhost:8082/druid | admin/admin | 数据库连接池监控 |

## 🔑 登录方式

**⚠️ 重要提示**：本系统已完全采用 **OAuth2 单点登录（SSO）** 模式，不再支持传统的用户名密码直接登录。

所有应用必须通过 OAuth2 授权流程接入认证中心。详细说明请查看：[SSO.md](SSO.md)

### 快速登录流程

1. **引导用户到授权端点**
```
GET /oauth2/authorize?response_type=code&client_id=main-app-client&redirect_uri=http://localhost:8080/callback&scope=read,write&state=xyz
```

2. **用户登录并授权**（自动跳转到 SSO 登录页面）

3. **获取授权码**（重定向回应用）
```
http://localhost:8080/callback?code=授权码&state=xyz
```

4. **换取访问令牌**
```http
POST http://localhost:8081/oauth2/token
Content-Type: application/json

{
  "grant_type": "authorization_code",
  "code": "授权码",
  "client_id": "main-app-client",
  "client_secret": "secret123",
  "redirect_uri": "http://localhost:8080/callback"
}
```

5. **使用访问令牌访问资源**
```http
GET http://localhost:8081/oauth2/userinfo
Authorization: Bearer {access_token}
```

### 通用接口

#### 1. 获取当前用户信息
```http
GET http://localhost:8081/auth/info
Authorization: Bearer {token}
```

#### 2. 用户登出
```http
POST http://localhost:8081/auth/logout
```

### OAuth2 单点登录接口

#### 1. OAuth2 授权（获取授权码）
```http
GET http://localhost:8081/oauth2/authorize?response_type=code&client_id=main-app-client&redirect_uri=http://localhost:8080/callback&scope=read,write&state=xyz
```

#### 2. 获取访问令牌
```http
POST http://localhost:8081/oauth2/token
Content-Type: application/json

{
  "grant_type": "authorization_code",
  "code": "授权码",
  "client_id": "main-app-client",
  "client_secret": "secret123",
  "redirect_uri": "http://localhost:8080/callback"
}
```

#### 3. 刷新访问令牌
```http
POST http://localhost:8081/oauth2/token
Content-Type: application/json

{
  "grant_type": "refresh_token",
  "refresh_token": "刷新令牌",
  "client_id": "main-app-client",
  "client_secret": "secret123"
}
```

#### 4. 获取用户信息（OAuth2资源端点）
```http
GET http://localhost:8081/oauth2/userinfo
Authorization: Bearer {access_token}
```

### 用户平台关联接口

#### 1. 绑定平台账号
```http
POST http://localhost:8081/auth/platform/bind
Authorization: Bearer {token}
Content-Type: application/json

{
  "platformId": "2",
  "platformUserId": "user123",
  "platformUsername": "测试用户"
}
```

#### 2. 解绑平台账号
```http
POST http://localhost:8081/auth/platform/unbind?platformId=2
Authorization: Bearer {token}
```

#### 3. 获取已绑定平台列表
```http
GET http://localhost:8081/auth/platform/bound
Authorization: Bearer {token}
```

### 用户接口

#### 1. 根据用户名获取用户
```http
GET http://localhost:8080/user/getByUsername/{username}
Authorization: Bearer {token}
```

#### 2. 创建用户
```http
POST http://localhost:8080/user/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "test",
  "password": "123456",
  "nickname": "测试用户"
}
```

### 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| user | 123456 | 普通用户 |

## 👥 用户管理

本系统提供完整的用户注册与管理解决方案，支持三种用户注册方式：

### 1. 用户自助注册 📝

用户可以通过前端注册页面自行创建账户。

**访问地址**：http://localhost:8082/self-register.html

**特性**：
- 图形验证码保护
- 实时用户名检查
- 注册成功自动跳转登录

### 2. 第三方平台注册 🔌

第三方系统可通过安全的API直接注册用户。

**接口地址**：`POST /api/third-party/register`

**特性**：
- API Key + 签名双重验证
- 防重放攻击（时间戳验证）
- 智能处理用户名冲突
- 自动绑定平台关系

### 3. 管理员创建用户 👨‍💼

管理员可通过管理后台创建和维护用户。

**访问地址**：http://localhost:8082/admin-user-management.html

**功能**：
- 用户CRUD管理
- 搜索和分页
- 启用/禁用用户
- 实时统计数据

**详细说明**：请查看 [用户注册与管理指南](docs/USER_MANAGEMENT.md)

## 📚 文档

### API 文档

启动服务后，访问以下地址查看API文档：

- 网关服务文档：http://localhost:8080/doc.html
- 认证服务文档：http://localhost:8081/doc.html
- 用户服务文档：http://localhost:8082/doc.html

### 核心文档

**⚠️ 系统已采用纯 OAuth2 SSO 登录模式**

1. **[SSO.md](SSO.md)** - SSO 单点登录服务完整指南（必读）
   - OAuth2 协议实现
   - 授权码模式流程
   - 客户端接入指南
   - 用户账号关联
   - 跨平台单点登录
   - 安全最佳实践

2. **[USER_MANAGEMENT.md](USER_MANAGEMENT.md)** - 用户注册与管理指南（新增）
   - 用户自助注册流程
   - 第三方平台注册API
   - 管理员用户管理
   - API签名与安全
   - 数据库表结构
   - 最佳实践建议

3. **[SKYWALKING.md](SKYWALKING.md)** - SkyWalking 分布式追踪服务
   - 功能特性和使用场景
   - 快速开始指南
   - UI 功能说明
   - 性能监控和追踪分析

4. **[SETUP.md](SETUP.md)** - 环境配置指南
   - 环境要求和安装
   - IDE 配置
   - 常见问题解决

5. **[DEPLOY.md](DEPLOY.md)** - 部署指南
   - 本地开发部署
   - Docker 部署
   - 生产环境部署
   - 监控和备份策略

## 🔧 配置说明

### Nacos配置

所有服务都集成了Nacos配置中心，支持配置动态刷新。

**配置文件命名规则**：
- `${spring.application.name}.yml` - 服务专属配置
- `common-config.yml` - 公共配置

### 认证配置

**Sa-Token配置**

Sa-Token用于统一认证鉴权：
- Token存储：Redis
- Token有效期：7天
- 支持并发登录控制

**OAuth2 单点登录配置**

基于 OAuth2 协议实现的单点登录系统：
- 授权码模式（Authorization Code）
- 支持多平台接入
- 用户账号关联
- 访问令牌和刷新令牌分离
- 支持自动授权和用户确认授权

**测试客户端信息**：
- Client ID: `main-app-client`
- Client Secret: `secret123`
- Redirect URI: `http://localhost:8080/callback`

### Sentinel限流规则

Sentinel提供流量控制和熔断降级功能：
- QPS限流
- 线程数限流  
- 熔断降级
- 系统自适应保护

## 📊 监控体系

### Prometheus + Grafana

1. **访问Prometheus**: http://localhost:9090
2. **访问Grafana**: http://localhost:3000 (admin/admin)
3. **配置数据源**：添加Prometheus数据源 `http://prometheus:9090`
4. **导入Dashboard**：推荐使用JVM Micrometer Dashboard

### Sentinel Dashboard

1. **访问控制台**: http://localhost:8858
2. **登录账号**: sentinel/sentinel
3. **查看实时监控**：流量、QPS、响应时间等
4. **配置限流规则**：支持动态配置

### SkyWalking 分布式追踪

1. **访问UI**: http://localhost:8088
2. **功能**：
   - 📊 服务拓扑图：可视化服务间调用关系
   - 🔍 追踪查询：查看完整的调用链路
   - 📈 性能监控：服务、端点、数据库性能
   - ⚠️ 异常追踪：快速定位错误
   - 📝 日志关联：关联日志与追踪

**详细使用说明**: [SKYWALKING.md](SKYWALKING.md)

## 🗂️ 项目结构

```
my-framework/
├── framework-common/              # 公共模块
│   ├── src/main/java/
│   │   └── com/myframework/common/
│   │       ├── config/           # 配置类
│   │       ├── constant/         # 常量
│   │       ├── domain/           # 公共实体
│   │       ├── exception/        # 异常处理
│   │       ├── result/           # 统一响应
│   │       └── util/             # 工具类
│   └── pom.xml
├── framework-api/                 # API模块
│   ├── src/main/java/
│   │   └── com/myframework/api/
│   │       ├── auth/             # 认证API
│   │       └── user/             # 用户API
│   └── pom.xml
├── framework-gateway/             # 网关服务
│   ├── src/main/java/
│   │   └── com/myframework/gateway/
│   │       ├── config/           # 配置
│   │       ├── filter/           # 过滤器
│   │       └── handler/          # 异常处理
│   ├── Dockerfile
│   └── pom.xml
├── framework-auth/                # 认证服务
│   ├── src/main/java/
│   │   └── com/myframework/auth/
│   │       ├── controller/       # 控制器
│   │       ├── service/          # 服务层
│   │       ├── entity/           # 实体类（OAuth2、平台）
│   │       ├── mapper/           # Mapper接口
│   │       └── dto/              # 数据传输对象
│   ├── Dockerfile
│   └── pom.xml
├── framework-user/                # 用户服务
│   ├── src/main/java/
│   │   └── com/myframework/user/
│   │       ├── controller/       # 控制器
│   │       ├── entity/           # 实体类
│   │       ├── mapper/           # Mapper接口
│   │       └── service/          # 服务层
│   ├── Dockerfile
│   └── pom.xml
├── docker/                        # Docker配置
│   ├── mysql/                    # MySQL配置
│   ├── redis/                    # Redis配置
│   └── prometheus/               # Prometheus配置
├── docker-compose.yml             # Docker编排文件
├── build.sh                       # 构建脚本
├── start.sh                       # 启动脚本
├── stop.sh                        # 停止脚本
├── pom.xml                        # 父POM
└── README.md                      # 项目文档
```

## 🔐 安全说明

### 认证流程

**系统采用纯 OAuth2 单点登录流程**：

1. 用户访问第三方应用
2. 应用引导用户到 SSO 授权端点
3. 用户在 SSO 中心登录（如已登录则跳过）
4. 用户确认授权（可配置自动授权）
5. SSO 生成授权码并重定向回应用
6. 应用使用授权码换取访问令牌
7. 应用使用访问令牌获取用户信息
8. 用户登录成功，可访问应用资源

### Token说明

**OAuth2 Token**（主要）：
- **Access Token**: 访问令牌，有效期默认1小时（可配置）
- **Refresh Token**: 刷新令牌，有效期默认7天（可配置）
- 支持令牌撤销和验证
- 存储在数据库中，支持多平台管理

### 多平台用户关联

系统支持用户在多个平台间关联账号：
- **账号绑定**: 用户可以将多个平台账号绑定到统一的 SSO 账号
- **账号隔离**: 不同平台的用户数据相互隔离
- **统一登录**: 一次登录，多个平台免登录访问
- **灵活管理**: 支持账号绑定和解绑操作

## 🚀 性能优化

### 数据库优化
- 使用Druid连接池
- 配置慢SQL监控
- 启用MyBatis Plus查询缓存

### 缓存优化
- Redis缓存用户会话
- 使用Redisson分布式锁
- 配置合理的缓存过期时间

### 接口优化
- Sentinel流量控制
- Gateway响应式编程
- 异步处理耗时操作

## 📖 开发指南

### 添加新的微服务

1. 创建新模块（参考framework-user）
2. 添加到父POM的modules中
3. 配置Nacos服务发现
4. 在framework-api中定义Feign接口
5. 在Gateway中配置路由规则
6. 创建Dockerfile
7. 添加到docker-compose.yml

### 自定义配置

1. 在Nacos中创建配置文件
2. 在服务的bootstrap.yml中引用
3. 使用`@RefreshScope`支持动态刷新

### API文档编写

使用Knife4j注解：
```java
@Tag(name = "用户管理", description = "用户增删改查")
@Operation(summary = "获取用户信息")
```

## ❓ 常见问题

### 1. Nacos启动失败
- 检查MySQL是否正常启动
- 确认端口8848未被占用
- 查看日志：`docker-compose logs nacos`

### 2. 服务注册失败
- 确认Nacos已启动完成
- 检查服务配置中的Nacos地址
- 查看服务日志

### 3. Token验证失败
- 检查Redis是否正常运行
- 确认Token格式正确（Bearer {token}）
- 查看Token是否过期

### 4. 数据库连接失败
- 检查MySQL容器状态
- 确认数据库初始化脚本已执行
- 验证数据库用户名密码

## 📝 更新日志

### v1.3.0 (最新) - 集成 SkyWalking 分布式追踪
- ✅ **新功能**：集成 Apache SkyWalking 9.7.0
- ✅ 分布式追踪：自动追踪服务间调用链路
- ✅ 性能监控：实时监控服务性能指标
- ✅ 服务拓扑：自动生成服务依赖关系图
- ✅ 数据库监控：追踪 SQL 执行情况
- ✅ 异常追踪：快速定位错误源头
- ✅ 详细文档：[SKYWALKING.md](SKYWALKING.md)

### v1.2.0 - 纯 OAuth2 SSO 模式
- ✅ **重大变更**：移除传统用户名密码登录，改为纯 OAuth2 SSO 模式
- ✅ 优化架构：用户相关数据库操作移至 user 模块
- ✅ 通过 Dubbo RPC 实现服务间通信
- ✅ 新增 SSO_ONLY_LOGIN.md 纯 OAuth2 登录说明文档
- ✅ 更新所有相关文档和示例

### v1.1.0
- ✅ 实现 OAuth2 单点登录（SSO）功能
- ✅ 支持多平台接入和用户账号关联
- ✅ 完善授权码模式和令牌管理
- ✅ 添加平台管理和用户绑定功能
- ✅ 新增 SSO 详细使用文档

### v1.0.0
- 基础微服务框架
- Sa-Token + JWT 认证
- 网关路由和服务治理
- 监控和容器化部署

## 📝 待办事项

- [x] ~~实现链路追踪（SkyWalking）~~ ✅ 已完成
- [ ] 实现 OAuth2 其他授权模式（密码模式、客户端模式）
- [ ] 添加 SSO 登录页面前端界面
- [ ] 集成第三方登录（微信、支付宝等）
- [ ] 集成消息队列（RocketMQ/Kafka）
- [ ] 添加分布式事务（Seata）
- [ ] 添加更多业务模块
- [ ] 完善单元测试
- [ ] 添加CI/CD流程
- [ ] SkyWalking 自定义告警规则

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License

## 👥 联系方式

如有问题或建议，请提交Issue。

---

**⭐ 如果这个项目对你有帮助，请给个Star支持一下！**

