# 📜 脚本目录说明

本目录包含项目的所有脚本文件，按环境分类存放。

---

## 📁 目录结构

```
scripts/
├── dev/                    # 开发环境脚本
│   ├── build.sh           # 构建脚本 (Linux/Mac)
│   ├── build.bat          # 构建脚本 (Windows)
│   ├── start.sh           # 启动脚本 (Linux/Mac)
│   ├── start.bat          # 启动脚本 (Windows)
│   ├── stop.sh            # 停止脚本 (Linux/Mac)
│   ├── stop.bat           # 停止脚本 (Windows)
│   ├── check-env.sh       # 环境检查 (Linux/Mac)
│   └── check-env.bat      # 环境检查 (Windows)
│
├── prod/                   # 生产环境脚本
│   ├── deploy-prod.sh     # 生产部署 (Linux/Mac)
│   ├── deploy-prod.bat    # 生产部署 (Windows)
│   ├── health-check.sh    # 健康检查 (Linux/Mac)
│   └── health-check.bat   # 健康检查 (Windows)
│
└── test/                   # 测试环境脚本
    ├── run-tests.sh       # 运行测试 (Linux/Mac)
    └── run-tests.bat      # 运行测试 (Windows)
```

---

## 🚀 使用指南

### 开发环境（Dev）

#### 1. 环境检查
```bash
# Linux/Mac
./scripts/dev/check-env.sh

# Windows
scripts\dev\check-env.bat
```

#### 2. 构建项目
```bash
# Linux/Mac
./scripts/dev/build.sh

# Windows
scripts\dev\build.bat
```

#### 3. 启动服务
```bash
# Linux/Mac
./scripts/dev/start.sh

# Windows
scripts\dev\start.bat
```

#### 4. 停止服务
```bash
# Linux/Mac
./scripts/dev/stop.sh

# Windows
scripts\dev\stop.bat
```

---

### 生产环境（Prod）

#### 1. 部署到生产环境

**前置要求**：
- 创建 `.env.prod` 配置文件
- 配置生产环境参数

```bash
# Linux/Mac
./scripts/prod/deploy-prod.sh

# Windows
scripts\prod\deploy-prod.bat
```

**脚本功能**：
- ✅ 检查环境和配置
- ✅ 停止旧服务
- ✅ 自动备份数据
- ✅ 构建生产镜像
- ✅ 启动新服务
- ✅ 执行健康检查

#### 2. 健康检查
```bash
# Linux/Mac
./scripts/prod/health-check.sh

# Windows
scripts\prod\health-check.bat
```

**检查项目**：
- Gateway (端口 8080)
- Auth Service (端口 8081)
- User Service (端口 8082)
- Nacos (端口 8848)
- SkyWalking UI (端口 8088)

---

### 测试环境（Test）

#### 运行所有测试
```bash
# Linux/Mac
./scripts/test/run-tests.sh

# Windows
scripts\test\run-tests.bat
```

**测试内容**：
- 单元测试
- 集成测试
- 生成测试报告

---

## 📝 脚本说明

### 开发环境脚本

| 脚本 | 功能 | 说明 |
|------|------|------|
| `check-env.sh/bat` | 环境检查 | 检查 JDK、Maven、Docker 等 |
| `build.sh/bat` | 构建项目 | Maven 编译并打包 |
| `start.sh/bat` | 启动服务 | 启动所有 Docker 容器 |
| `stop.sh/bat` | 停止服务 | 停止所有 Docker 容器 |

### 生产环境脚本

| 脚本 | 功能 | 说明 |
|------|------|------|
| `deploy-prod.sh/bat` | 生产部署 | 完整的生产环境部署流程 |
| `health-check.sh/bat` | 健康检查 | 检查所有服务健康状态 |

### 测试环境脚本

| 脚本 | 功能 | 说明 |
|------|------|------|
| `run-tests.sh/bat` | 运行测试 | 执行所有单元测试和集成测试 |

---

## 🔧 配置文件

### 生产环境配置（`.env.prod`）

创建 `.env.prod` 文件并配置以下参数：

```bash
# 数据库配置
MYSQL_HOST=your-prod-mysql-host
MYSQL_PORT=3306
MYSQL_DATABASE=my_framework
MYSQL_USER=your-prod-user
MYSQL_PASSWORD=your-prod-password

# Redis 配置
REDIS_HOST=your-prod-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-prod-password

# Nacos 配置
NACOS_SERVER_ADDR=your-prod-nacos:8848

# JVM 参数
JAVA_OPTS=-Xms1g -Xmx2g
```

---

## 💡 最佳实践

### 1. 权限设置

Linux/Mac 系统下，赋予脚本执行权限：
```bash
chmod +x scripts/dev/*.sh
chmod +x scripts/prod/*.sh
chmod +x scripts/test/*.sh
```

### 2. 日志查看

查看服务日志：
```bash
# 开发环境
docker-compose logs -f [service-name]

# 生产环境
docker-compose -f docker-compose.prod.yml logs -f [service-name]
```

### 3. 数据备份

生产环境部署脚本会自动备份数据到 `backups/` 目录，建议：
- 定期清理旧备份
- 将重要备份存储到远程
- 测试备份恢复流程

### 4. 回滚策略

如果部署失败，可以使用备份恢复：
```bash
# 停止服务
docker-compose -f docker-compose.prod.yml down

# 恢复数据
cp -r backups/[backup-dir]/mysql docker/mysql/data

# 启动服务
docker-compose -f docker-compose.prod.yml up -d
```

---

## 🐛 故障排查

### 问题1：脚本无法执行

**Linux/Mac**：
```bash
# 检查权限
ls -l scripts/dev/build.sh

# 赋予执行权限
chmod +x scripts/dev/build.sh
```

**Windows**：
- 以管理员身份运行 CMD 或 PowerShell
- 检查执行策略：`Get-ExecutionPolicy`

### 问题2：健康检查失败

1. 查看服务日志：`docker-compose logs [service]`
2. 检查端口占用：`netstat -ano | findstr [port]`
3. 确认服务配置正确

### 问题3：部署失败

1. 检查 `.env.prod` 配置
2. 查看构建日志
3. 确认网络连接正常
4. 验证 Docker 资源充足

---

## 📚 相关文档

- [项目说明](../docs/README.md)
- [部署文档](../docs/DEPLOY.md)
- [SkyWalking 使用指南](../docs/SKYWALKING.md)

---

**提示**: 根据实际环境修改脚本中的配置参数。

