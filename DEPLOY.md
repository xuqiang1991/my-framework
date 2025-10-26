# 部署指南

## 目录
- [环境准备](#环境准备)
- [本地开发部署](#本地开发部署)
- [Docker部署](#docker部署)
- [生产环境部署](#生产环境部署)
- [常见问题](#常见问题)

## 环境准备

### 必需软件

#### Windows系统
1. **JDK 21**
   - 下载地址：https://www.oracle.com/java/technologies/downloads/#java21
   - 配置JAVA_HOME环境变量
   - 验证：`java -version`

2. **Maven 3.8+**
   - 下载地址：https://maven.apache.org/download.cgi
   - 配置MAVEN_HOME环境变量
   - 验证：`mvn -version`

3. **Docker Desktop**
   - 下载地址：https://www.docker.com/products/docker-desktop
   - 启用WSL2（推荐）
   - 验证：`docker --version` 和 `docker-compose --version`

4. **Git**
   - 下载地址：https://git-scm.com/download/win
   - 验证：`git --version`

#### Linux/Mac系统
```bash
# 安装JDK 21
sudo apt install openjdk-21-jdk  # Ubuntu/Debian
brew install openjdk@21          # Mac

# 安装Maven
sudo apt install maven           # Ubuntu/Debian
brew install maven               # Mac

# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo apt install docker-compose  # Ubuntu/Debian
brew install docker-compose      # Mac
```

### 端口占用检查

确保以下端口未被占用：
- 3000 (Grafana)
- 3306 (MySQL)
- 6379 (Redis)
- 8080 (Gateway)
- 8081 (Auth Service)
- 8082 (User Service)
- 8848, 9848 (Nacos)
- 8858 (Sentinel)
- 9090 (Prometheus)

检查端口命令：
```bash
# Windows
netstat -ano | findstr "8080"

# Linux/Mac
lsof -i :8080
```

## 本地开发部署

### 方式一：IDE运行（推荐用于开发）

1. **启动基础设施**
```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

或手动启动：
```bash
docker-compose up -d mysql redis nacos sentinel
```

2. **等待Nacos启动完成**
```bash
# 查看日志
docker-compose logs -f nacos

# 等待看到 "Nacos started successfully"
```

3. **在IDE中启动服务**
   - 启动 `GatewayApplication`
   - 启动 `AuthApplication`
   - 启动 `UserApplication`

4. **验证服务**
```bash
# 访问Nacos控制台
http://localhost:8848/nacos
# 登录：nacos/nacos
# 查看服务列表，确认3个服务都已注册
```

### 方式二：命令行运行

1. **编译项目**
```bash
# Windows
mvn clean package -DskipTests

# Linux/Mac
mvn clean package -DskipTests
```

2. **启动基础设施**
```bash
docker-compose up -d mysql redis nacos sentinel
```

3. **等待60秒后启动服务**
```bash
# 启动网关
cd framework-gateway
mvn spring-boot:run

# 新开终端，启动认证服务
cd framework-auth
mvn spring-boot:run

# 新开终端，启动用户服务
cd framework-user
mvn spring-boot:run
```

## Docker部署

### 完整Docker部署（推荐用于测试/生产）

1. **构建项目并创建镜像**
```bash
# Windows
build.bat

# Linux/Mac
chmod +x build.sh
./build.sh
```

这个脚本会：
- 执行Maven构建
- 为每个微服务创建Docker镜像

2. **启动所有服务**
```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

3. **验证部署**
```bash
# 查看所有容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f gateway
docker-compose logs -f auth
docker-compose logs -f user
```

4. **测试接口**
```bash
# 测试登录
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 单独重启某个服务

```bash
# 重启网关服务
docker-compose restart gateway

# 重新构建并启动
docker-compose up -d --build gateway
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f gateway
docker-compose logs -f auth
docker-compose logs -f mysql
docker-compose logs -f nacos
```

### 停止服务

```bash
# Windows
stop.bat

# Linux/Mac
chmod +x stop.sh
./stop.sh

# 或直接使用docker-compose
docker-compose down

# 停止并删除所有数据卷（谨慎使用）
docker-compose down -v
```

## 生产环境部署

### 1. 修改配置

#### 修改docker-compose.yml

```yaml
# 修改数据库密码
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: your_secure_password

# 修改Redis配置（如需密码）
redis:
  command: redis-server --requirepass your_redis_password

# 修改服务环境变量
auth:
  environment:
    SPRING_PROFILES_ACTIVE: prod
    SPRING_DATASOURCE_PASSWORD: your_secure_password
```

#### 修改Nacos配置

1. 登录Nacos控制台
2. 创建生产环境命名空间（prod）
3. 修改各服务的配置文件
4. 配置数据库连接、Redis连接等

### 2. 安全加固

#### 修改默认密码
- MySQL root密码
- Redis密码
- Nacos控制台密码（nacos/nacos）
- Sentinel控制台密码
- Grafana密码

#### 配置防火墙
```bash
# Ubuntu/Debian
sudo ufw allow 8080/tcp  # Gateway
sudo ufw enable

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

#### 配置HTTPS
使用Nginx作为反向代理配置SSL证书

### 3. 性能优化

#### 调整JVM参数

修改各服务的Dockerfile：
```dockerfile
ENV JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### 调整数据库连接池

在Nacos配置中心修改Druid配置：
```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
```

#### 调整Redis连接池

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
```

### 4. 监控告警

#### 配置Prometheus告警规则

创建 `docker/prometheus/alert.rules.yml`：
```yaml
groups:
  - name: service_alerts
    interval: 30s
    rules:
      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "服务 {{ $labels.instance }} 已停止"
```

#### 配置Grafana Dashboard

1. 登录Grafana (http://localhost:3000)
2. 添加Prometheus数据源
3. 导入JVM监控Dashboard（ID: 4701）
4. 导入Spring Boot Dashboard（ID: 12900）

### 5. 备份策略

#### 数据库备份

```bash
# 手动备份
docker exec my-framework-mysql mysqldump -uroot -proot my_framework > backup.sql

# 定时备份（crontab）
0 2 * * * docker exec my-framework-mysql mysqldump -uroot -proot my_framework > /backup/db_$(date +\%Y\%m\%d).sql
```

#### Redis备份

```bash
# 手动备份
docker exec my-framework-redis redis-cli SAVE
docker cp my-framework-redis:/data/dump.rdb ./backup/

# 或配置AOF持久化（已在redis.conf中配置）
```

## 常见问题

### 1. Nacos启动失败

**问题**：Nacos容器反复重启

**解决**：
```bash
# 查看日志
docker-compose logs nacos

# 确认MySQL已启动
docker-compose ps mysql

# 手动创建nacos数据库
docker exec -it my-framework-mysql mysql -uroot -proot
> CREATE DATABASE nacos;
> exit;

# 重启Nacos
docker-compose restart nacos
```

### 2. 服务注册失败

**问题**：服务无法注册到Nacos

**解决**：
```bash
# 检查Nacos是否正常
curl http://localhost:8848/nacos/v1/console/health/readiness

# 检查服务配置
# 确认 application.yml 中的 nacos 地址正确

# 查看服务日志
docker-compose logs auth
```

### 3. 数据库连接失败

**问题**：服务启动时报数据库连接错误

**解决**：
```bash
# 检查MySQL状态
docker-compose ps mysql

# 检查数据库是否创建
docker exec -it my-framework-mysql mysql -uroot -proot
> SHOW DATABASES;
> USE my_framework;
> SHOW TABLES;

# 检查连接字符串
# jdbc:mysql://mysql:3306/my_framework (Docker环境)
# jdbc:mysql://localhost:3306/my_framework (本地环境)
```

### 4. Redis连接失败

**问题**：Redis连接超时

**解决**：
```bash
# 检查Redis状态
docker-compose ps redis

# 测试连接
docker exec -it my-framework-redis redis-cli ping
# 应返回 PONG

# 检查Redis配置
# 本地环境：localhost:6379
# Docker环境：redis:6379
```

### 5. 网关404错误

**问题**：通过网关访问服务返回404

**解决**：
```bash
# 检查服务是否注册到Nacos
# 访问 http://localhost:8848/nacos

# 检查Gateway路由配置
# 查看 framework-gateway/application.yml

# 查看Gateway日志
docker-compose logs gateway
```

### 6. Token验证失败

**问题**：请求返回401 Unauthorized

**解决**：
```bash
# 检查Token格式
# 应为：Authorization: Bearer {token}

# 检查Redis是否正常
docker exec -it my-framework-redis redis-cli
> KEYS login:token:*

# 检查Sa-Token配置
# 查看 application.yml 中的 sa-token 配置
```

### 7. 内存不足

**问题**：Docker容器频繁OOM

**解决**：
```yaml
# 在docker-compose.yml中限制内存
services:
  auth:
    mem_limit: 1g
    environment:
      JAVA_OPTS: "-Xms512m -Xmx768m"
```

### 8. 磁盘空间不足

**问题**：Docker占用大量磁盘空间

**解决**：
```bash
# 清理未使用的容器、镜像、网络
docker system prune -a

# 清理未使用的数据卷
docker volume prune

# 查看空间占用
docker system df
```

## 健康检查

### 检查服务健康状态

```bash
# Gateway健康检查
curl http://localhost:8080/actuator/health

# Auth服务健康检查
curl http://localhost:8081/actuator/health

# User服务健康检查
curl http://localhost:8082/actuator/health
```

### 检查Prometheus指标

```bash
# Gateway指标
curl http://localhost:8080/actuator/prometheus

# Auth服务指标
curl http://localhost:8081/actuator/prometheus
```

## 性能测试

### 使用Apache Bench

```bash
# 安装ab
sudo apt install apache2-utils  # Ubuntu
brew install httpd              # Mac

# 登录测试
ab -n 1000 -c 10 -p login.json -T application/json \
  http://localhost:8080/auth/login

# login.json 内容
# {"username":"admin","password":"123456"}
```

### 使用JMeter

1. 下载JMeter
2. 导入测试计划
3. 配置线程组和HTTP请求
4. 运行测试并查看结果

## 升级指南

### 更新服务版本

```bash
# 1. 拉取最新代码
git pull

# 2. 重新构建
./build.sh

# 3. 滚动更新服务
docker-compose up -d --no-deps auth
docker-compose up -d --no-deps user
docker-compose up -d --no-deps gateway
```

### 数据库迁移

```bash
# 1. 备份数据库
docker exec my-framework-mysql mysqldump -uroot -proot my_framework > backup.sql

# 2. 执行迁移脚本
docker exec -i my-framework-mysql mysql -uroot -proot my_framework < migration.sql

# 3. 验证数据
docker exec -it my-framework-mysql mysql -uroot -proot my_framework
```

## 技术支持

遇到问题请：
1. 查看日志：`docker-compose logs -f [service-name]`
2. 检查健康状态：`curl http://localhost:8080/actuator/health`
3. 查看Nacos服务列表
4. 提交Issue到项目仓库

