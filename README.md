# My Framework - Spring Cloud Alibaba 微服务框架

[![JDK](https://img.shields.io/badge/JDK-21-green.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![SkyWalking](https://img.shields.io/badge/SkyWalking-9.7.0-orange.svg)](https://skywalking.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📝 项目简介

基于 **Spring Cloud Alibaba** 构建的现代化微服务框架，集成了主流的微服务技术栈，提供完整的微服务解决方案。

### ✨ 核心特性

- 🔐 **统一鉴权**: Sa-Token + JWT + OAuth2 单点登录（SSO）
- 🔍 **分布式追踪**: SkyWalking APM 全链路监控
- 🚪 **网关路由**: Spring Cloud Gateway + 动态路由
- 📡 **服务治理**: Nacos 服务注册与发现、配置中心
- 💾 **数据存储**: MySQL 8.0 + MyBatis Plus + Druid
- 🔥 **缓存方案**: Redis + 分布式缓存
- 📊 **监控体系**: Sentinel + Prometheus + Grafana + SkyWalking
- 🐳 **容器化**: Docker + Docker Compose 一键部署
- ☕ **最新技术**: JDK 21 + Spring Boot 3.2

---

## 📁 项目结构

```
my-framework/
├── docs/                          # 📚 项目文档
│   ├── README.md                 # 详细项目说明
│   ├── API.md                    # API 接口文档
│   ├── DEPLOY.md                 # 部署文档
│   ├── SKYWALKING.md            # SkyWalking 使用指南
│   ├── SSO.md                    # SSO 单点登录文档
│   └── ...                       # 其他文档
│
├── scripts/                       # 🔧 脚本文件
│   ├── dev/                      # 开发环境脚本
│   │   ├── build.sh/bat         # 构建脚本
│   │   ├── start.sh/bat         # 启动脚本
│   │   └── stop.sh/bat          # 停止脚本
│   ├── prod/                     # 生产环境脚本
│   │   ├── deploy-prod.sh/bat   # 生产部署
│   │   └── health-check.sh/bat  # 健康检查
│   └── test/                     # 测试环境脚本
│       └── run-tests.sh/bat     # 运行测试
│
├── framework-common/              # 公共模块
├── framework-api/                 # API 接口定义
├── framework-gateway/             # 网关服务
├── framework-auth/                # 认证服务
├── framework-user/                # 用户服务
│
├── docker/                        # Docker 配置
│   ├── mysql/                    # MySQL 配置
│   ├── redis/                    # Redis 配置
│   ├── skywalking/              # SkyWalking Agent
│   └── ...
│
├── docker-compose.yml            # Docker Compose 配置
└── pom.xml                       # Maven 父 POM
```

---

## 🚀 快速开始

### 方式一：使用脚本启动（推荐）

```bash
# 1. 下载 SkyWalking Agent
cd docker/skywalking
./download-agent.sh  # Windows: download-agent.bat
cd ../..

# 2. 构建项目
./scripts/dev/build.sh  # Windows: scripts\dev\build.bat

# 3. 启动服务
./scripts/dev/start.sh  # Windows: scripts\dev\start.bat

# 4. 停止服务
./scripts/dev/stop.sh   # Windows: scripts\dev\stop.bat
```

### 方式二：手动启动

```bash
# 1. 构建项目
mvn clean package -DskipTests

# 2. 启动服务
docker-compose up -d

# 3. 查看状态
docker-compose ps
```

---

## 📊 服务地址

| 服务 | 地址 | 账号/密码 |
|------|------|-----------|
| **SkyWalking UI** | http://localhost:8088 | - |
| 网关服务 | http://localhost:8080 | - |
| 认证服务 | http://localhost:8081 | - |
| 用户服务 | http://localhost:8082 | - |
| Nacos 控制台 | http://localhost:8848/nacos | nacos/nacos |
| Sentinel 控制台 | http://localhost:8858 | sentinel/sentinel |
| Grafana | http://localhost:3000 | admin/admin |

---

## 📚 文档导航

### 快速上手
- **[详细项目说明](docs/README.md)** - 完整的项目文档
- **[快速开始指南](docs/SKYWALKING_QUICKSTART.md)** - 5分钟快速体验

### 功能文档
- **[SkyWalking 使用指南](docs/SKYWALKING.md)** - 分布式追踪完整教程
- **[SSO 单点登录](docs/SSO.md)** - OAuth2 单点登录文档
- **[API 文档](docs/API.md)** - 接口文档
- **[部署文档](docs/DEPLOY.md)** - 生产环境部署

### 开发文档
- **[脚本说明](scripts/README.md)** - 脚本使用指南
- **[代码优化报告](docs/CODE_OPTIMIZATION_REPORT.md)** - 代码优化详情
- **[贡献指南](docs/CONTRIBUTING.md)** - 如何贡献代码

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | Java 开发工具包 |
| Spring Boot | 3.2.5 | 基础框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | 阿里微服务套件 |
| **SkyWalking** | **9.7.0** | **分布式追踪 APM** |
| Nacos | 2.3.0 | 服务注册与配置中心 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x | 缓存数据库 |
| Elasticsearch | 8.11.0 | SkyWalking 存储后端 |

---

## 🎯 核心功能

### 1. 分布式追踪（SkyWalking）
- 📊 服务拓扑图可视化
- 🔍 全链路追踪
- 📈 性能监控和分析
- 💾 数据库监控
- ⚠️ 异常追踪

### 2. OAuth2 单点登录
- 🔐 统一认证授权
- 👥 多平台支持
- 🔗 账号关联
- 🎫 Token 管理

### 3. 服务治理
- 🚪 统一网关
- 📡 服务注册发现
- ⚙️ 配置中心
- 🛡️ 限流熔断

---

## 📝 更新日志

### v1.3.0 (最新) - SkyWalking 集成
- ✅ 集成 Apache SkyWalking 9.7.0
- ✅ 全链路分布式追踪
- ✅ 性能监控和优化
- ✅ 完整的文档体系
- ✅ 优化项目结构（文档和脚本分类）

### v1.2.5 - 代码优化
- ✅ 性能优化：密码编码器单例化
- ✅ 代码规范：构造器注入
- ✅ 新增：BusinessConstant 常量类

### v1.2.0 - OAuth2 SSO
- ✅ 纯 OAuth2 单点登录模式
- ✅ 多平台支持和账号关联

---

## 🎓 学习路径

### 新手入门
1. 阅读 [项目说明](docs/README.md)
2. 跟随 [快速开始](#-快速开始) 启动项目
3. 体验 [SkyWalking 追踪](docs/SKYWALKING_QUICKSTART.md)

### 进阶学习
1. 深入学习 [SkyWalking](docs/SKYWALKING.md)
2. 了解 [OAuth2 SSO](docs/SSO.md)
3. 学习 [API 使用](docs/API.md)

### 生产部署
1. 查看 [部署文档](docs/DEPLOY.md)
2. 使用 [生产脚本](scripts/prod/)
3. 配置 [健康检查](scripts/prod/health-check.sh)

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

详见：[CONTRIBUTING.md](docs/CONTRIBUTING.md)

---

## 📄 许可证

MIT License

---

## 📞 联系方式

如有问题或建议，请提交 Issue。

---

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

---

## 🔗 相关链接

- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba)
- [Apache SkyWalking](https://skywalking.apache.org/)
- [Nacos](https://nacos.io/)
- [Sa-Token](https://sa-token.cc/)
