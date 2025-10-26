# My Framework - Spring Cloud Alibaba 微服务框架

## 📝 项目简介

这是一个基于 **Spring Cloud Alibaba** 构建的现代化微服务框架，集成了主流的微服务技术栈，提供了完整的微服务解决方案。

### ✨ 核心特性

- 🔐 **统一鉴权**: Sa-Token + JWT + 单点登录（SSO）
- 🚪 **网关路由**: Spring Cloud Gateway + 动态路由
- 📡 **服务治理**: Nacos服务注册与发现、配置中心
- 💾 **数据存储**: MySQL 8.0 + MyBatis Plus + Druid
- 🔥 **缓存方案**: Redis + 分布式缓存
- 📊 **监控体系**: Sentinel + Prometheus + Grafana
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

2. **构建项目**
```bash
chmod +x build.sh
./build.sh
```

3. **启动所有服务**
```bash
chmod +x start.sh
./start.sh
```

4. **等待服务启动完成（约2-3分钟）**

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
chmod +x stop.sh
./stop.sh
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
| MySQL | localhost:3306 | root/root | 数据库 |
| Redis | localhost:6379 | - | 缓存 |
| Druid监控 | http://localhost:8082/druid | admin/admin | 数据库连接池监控 |

## 🔑 API接口

### 认证接口

#### 1. 用户登录
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

#### 2. 用户登出
```http
POST http://localhost:8080/auth/logout
Authorization: Bearer {token}
```

#### 3. 刷新Token
```http
POST http://localhost:8080/auth/refresh?token={refresh_token}
```

#### 4. 获取当前用户信息
```http
GET http://localhost:8080/auth/info
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

## 📚 API文档

启动服务后，访问以下地址查看API文档：

- 网关服务文档：http://localhost:8080/doc.html
- 认证服务文档：http://localhost:8081/doc.html
- 用户服务文档：http://localhost:8082/doc.html

## 🔧 配置说明

### Nacos配置

所有服务都集成了Nacos配置中心，支持配置动态刷新。

**配置文件命名规则**：
- `${spring.application.name}.yml` - 服务专属配置
- `common-config.yml` - 公共配置

### Sa-Token配置

Sa-Token用于统一认证鉴权：
- Token存储：Redis
- Token有效期：7天
- 支持单点登录（SSO）
- 支持并发登录控制

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
│   │       └── service/          # 服务层
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

1. 用户通过 `/auth/login` 接口登录
2. 认证服务验证用户名密码
3. 验证成功后生成Token（Sa-Token + JWT）
4. 将Token返回给客户端
5. 客户端后续请求携带Token
6. 网关验证Token有效性
7. Token有效则放行，无效则返回401

### Token说明

- **AccessToken**: 用于API调用，有效期7天
- **RefreshToken**: 用于刷新Token，基于JWT实现
- Token存储在Redis中，支持快速验证和注销

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

## 📝 待办事项

- [ ] 集成消息队列（RocketMQ/Kafka）
- [ ] 添加分布式事务（Seata）
- [ ] 实现链路追踪（SkyWalking）
- [ ] 添加更多业务模块
- [ ] 完善单元测试
- [ ] 添加CI/CD流程

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

## 📄 许可证

MIT License

## 👥 联系方式

如有问题或建议，请提交Issue。

---

**⭐ 如果这个项目对你有帮助，请给个Star支持一下！**

