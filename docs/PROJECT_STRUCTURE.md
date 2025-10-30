# 📁 项目结构说明

本文档详细说明了项目的目录结构和文件组织方式。

---

## 🗂️ 根目录结构

```
my-framework/
├── docs/                          # 📚 项目文档目录
├── scripts/                       # 🔧 脚本文件目录
├── docker/                        # 🐳 Docker 配置目录
├── framework-common/              # 🔧 公共模块
├── framework-api/                 # 📡 API 接口定义
├── framework-gateway/             # 🚪 网关服务
├── framework-auth/                # 🔐 认证服务
├── framework-user/                # 👤 用户服务
├── logs/                          # 📝 日志目录
├── docker-compose.yml            # Docker Compose 配置
├── pom.xml                       # Maven 父 POM
├── .gitignore                    # Git 忽略文件
└── README.md                     # 项目简介（此文件）
```

---

## 📚 docs/ - 文档目录

存放所有项目文档，包括使用指南、API 文档、部署文档等。

```
docs/
├── README.md                      # 详细项目说明（包含架构图和流程图）
├── PROJECT_STRUCTURE.md           # 项目结构说明（本文件）
├── SETUP.md                       # 环境配置指南
├── DEPLOY.md                      # 部署文档
├── API.md                         # API 接口文档
├── SKYWALKING.md                  # SkyWalking 服务说明和使用指南
└── SSO.md                         # SSO 单点登录服务说明
```

### 文档说明

#### 📖 核心文档
- **README.md** - 项目详细说明，包含架构图、技术栈、功能介绍、快速开始
- **PROJECT_STRUCTURE.md** - 本文件，项目结构和目录组织说明
- **SETUP.md** - 开发环境配置指南
- **DEPLOY.md** - 本地和生产环境部署指南

#### 📚 服务文档
- **API.md** - API 接口文档和使用示例
- **SKYWALKING.md** - SkyWalking 分布式追踪服务的详细说明和使用指南
- **SSO.md** - OAuth2 单点登录服务的完整说明和接入指南

---

## 🔧 scripts/ - 脚本目录

存放所有脚本文件，按环境分类组织。

```
scripts/
├── README.md                      # 脚本使用说明
├── dev/                           # 开发环境脚本
│   ├── build.sh                  # 构建脚本（Linux/Mac）
│   ├── build.bat                 # 构建脚本（Windows）
│   ├── start.sh                  # 启动脚本（Linux/Mac）
│   ├── start.bat                 # 启动脚本（Windows）
│   ├── stop.sh                   # 停止脚本（Linux/Mac）
│   ├── stop.bat                  # 停止脚本（Windows）
│   ├── check-env.sh              # 环境检查（Linux/Mac）
│   └── check-env.bat             # 环境检查（Windows）
├── prod/                          # 生产环境脚本
│   ├── deploy-prod.sh            # 生产部署（Linux/Mac）
│   ├── deploy-prod.bat           # 生产部署（Windows）
│   ├── health-check.sh           # 健康检查（Linux/Mac）
│   └── health-check.bat          # 健康检查（Windows）
└── test/                          # 测试环境脚本
    ├── run-tests.sh              # 运行测试（Linux/Mac）
    └── run-tests.bat             # 运行测试（Windows）
```

### 脚本说明

#### 开发环境（dev/）
- **build.sh/bat** - 编译打包项目
- **start.sh/bat** - 启动所有服务
- **stop.sh/bat** - 停止所有服务
- **check-env.sh/bat** - 检查开发环境

#### 生产环境（prod/）
- **deploy-prod.sh/bat** - 完整的生产部署流程（备份、构建、部署、健康检查）
- **health-check.sh/bat** - 检查所有服务健康状态

#### 测试环境（test/）
- **run-tests.sh/bat** - 运行所有单元测试和集成测试

---

## 🐳 docker/ - Docker 配置目录

存放 Docker 相关的配置文件和数据目录。

```
docker/
├── elasticsearch/                 # Elasticsearch 配置
│   └── data/                     # 数据目录（.gitignore）
├── grafana/                       # Grafana 配置
│   └── data/                     # 数据目录（.gitignore）
├── mysql/                         # MySQL 配置
│   ├── conf/                     # 配置文件
│   │   └── my.cnf
│   ├── data/                     # 数据目录（.gitignore）
│   └── init/                     # 初始化 SQL
│       └── init.sql
├── nacos/                         # Nacos 配置
│   └── logs/                     # 日志目录（.gitignore）
├── prometheus/                    # Prometheus 配置
│   ├── data/                     # 数据目录（.gitignore）
│   └── prometheus.yml            # 配置文件
├── redis/                         # Redis 配置
│   ├── conf/                     # 配置文件
│   │   └── redis.conf
│   └── data/                     # 数据目录（.gitignore）
└── skywalking/                    # SkyWalking 配置
    ├── agent/                    # Agent 文件（需下载，.gitignore）
    ├── README.md                 # Agent 配置说明
    ├── download-agent.sh         # 下载脚本（Linux/Mac）
    ├── download-agent.bat        # 下载脚本（Windows）
    └── .gitignore                # Git 忽略规则
```

---

## 🏗️ 微服务模块

### framework-common/ - 公共模块

```
framework-common/
├── src/
│   └── main/
│       └── java/
│           └── com/myframework/common/
│               ├── config/       # 配置类（Redis、安全等）
│               ├── constant/     # 常量定义
│               │   ├── BusinessConstant.java  # 业务常量
│               │   └── CommonConstant.java    # 公共常量
│               ├── domain/       # 公共实体
│               ├── exception/    # 异常处理
│               │   ├── BusinessException.java
│               │   └── GlobalExceptionHandler.java
│               ├── result/       # 统一响应
│               │   ├── Result.java
│               │   └── ResultCode.java
│               └── util/         # 工具类
│                   ├── JwtUtil.java
│                   └── RedisUtil.java
└── pom.xml
```

### framework-api/ - API 接口定义

```
framework-api/
├── src/
│   └── main/
│       └── java/
│           └── com/myframework/api/
│               ├── auth/         # 认证相关 API
│               │   ├── PlatformApi.java
│               │   └── dto/
│               └── user/         # 用户相关 API
│                   ├── UserPlatformApi.java
│                   └── dto/
└── pom.xml
```

### framework-gateway/ - 网关服务

```
framework-gateway/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/myframework/gateway/
│       │       ├── GatewayApplication.java
│       │       ├── config/       # 配置
│       │       ├── filter/       # 过滤器
│       │       └── handler/      # 异常处理
│       └── resources/
│           ├── application.yml
│           └── bootstrap.yml
├── Dockerfile
└── pom.xml
```

### framework-auth/ - 认证服务

```
framework-auth/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/myframework/auth/
│       │       ├── AuthApplication.java
│       │       ├── config/       # 配置类
│       │       ├── controller/   # 控制器
│       │       │   ├── AuthController.java
│       │       │   ├── OAuth2Controller.java
│       │       │   ├── SSOController.java
│       │       │   └── UserPlatformController.java
│       │       ├── service/      # 服务层
│       │       │   ├── AuthService.java
│       │       │   ├── OAuth2Service.java
│       │       │   └── PlatformService.java
│       │       ├── entity/       # 实体类
│       │       ├── mapper/       # Mapper 接口
│       │       ├── dto/          # 数据传输对象
│       │       └── rpc/          # RPC 实现
│       └── resources/
│           ├── application.yml
│           └── bootstrap.yml
├── Dockerfile
└── pom.xml
```

### framework-user/ - 用户服务

```
framework-user/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/myframework/user/
│       │       ├── UserApplication.java
│       │       ├── config/       # 配置类
│       │       ├── controller/   # 控制器
│       │       │   ├── UserController.java
│       │       │   └── UserRegisterController.java
│       │       ├── service/      # 服务层
│       │       │   ├── UserService.java
│       │       │   ├── UserRegisterService.java
│       │       │   └── UserPlatformService.java
│       │       ├── entity/       # 实体类
│       │       │   ├── User.java
│       │       │   └── UserPlatform.java
│       │       ├── mapper/       # Mapper 接口
│       │       ├── dto/          # 数据传输对象
│       │       └── rpc/          # RPC 实现
│       └── resources/
│           ├── application.yml
│           └── bootstrap.yml
├── Dockerfile
└── pom.xml
```

---

## 📝 配置文件

### docker-compose.yml
Docker Compose 配置文件，定义了所有服务的容器配置。

### pom.xml
Maven 父 POM 文件，统一管理依赖版本和构建配置。

### .gitignore
Git 忽略文件，定义了不需要提交到版本控制的文件和目录：
- 构建产物（target/）
- IDE 配置（.idea/, .vscode/）
- Docker 数据目录
- SkyWalking Agent
- 日志文件

---

## 🎯 目录组织原则

### 1. 文档集中管理
所有文档统一放在 `docs/` 目录，便于查找和维护。

### 2. 脚本分类存放
脚本按环境（dev/prod/test）分类，清晰明确。

### 3. 模块化设计
微服务模块独立，每个模块都有完整的结构：
- src/ - 源代码
- pom.xml - Maven 配置
- Dockerfile - Docker 镜像构建

### 4. 配置分离
Docker 相关配置统一在 `docker/` 目录，与代码分离。

### 5. 日志独立
日志文件统一输出到 `logs/` 目录。

---

## 🚀 最佳实践

### 添加新文档
1. 在 `docs/` 目录创建文档
2. 确定文档类型（核心文档/服务文档）
3. 更新本文件的文档列表

### 添加新脚本
1. 确定脚本用途（开发/生产/测试）
2. 在对应目录创建脚本
3. 添加 Linux/Mac (.sh) 和 Windows (.bat) 两个版本
4. 更新 `scripts/README.md`

### 添加新服务
1. 创建新模块（参考现有模块结构）
2. 添加到父 POM 的 modules
3. 创建 Dockerfile
4. 在 docker-compose.yml 中添加服务配置

---

## 📚 相关文档

- [项目说明（包含架构图）](README.md)
- [环境配置指南](SETUP.md)
- [部署文档](DEPLOY.md)
- [API接口文档](API.md)
- [SkyWalking服务说明](SKYWALKING.md)
- [SSO服务说明](SSO.md)
- [脚本使用指南](../scripts/README.md)

---

**提示**: 保持目录结构清晰，有助于项目的长期维护和团队协作。

