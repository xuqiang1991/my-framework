# AI机器人服务部署指南

## 📋 目录

- [环境准备](#环境准备)
- [数据库初始化](#数据库初始化)
- [配置说明](#配置说明)
- [本地开发](#本地开发)
- [Docker部署](#docker部署)
- [生产环境部署](#生产环境部署)
- [常见问题](#常见问题)

## 🔧 环境准备

### 1. 基础环境要求

- **JDK**: 21+
- **Maven**: 3.9+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Nacos**: 2.3.0+

### 2. OpenAI API配置

需要准备以下其中之一：

- **OpenAI官方**: [https://platform.openai.com](https://platform.openai.com)
- **Azure OpenAI**: [https://azure.microsoft.com/openai](https://azure.microsoft.com/openai)
- **阿里云通义千问**: [https://dashscope.aliyun.com](https://dashscope.aliyun.com)
- **智谱AI**: [https://open.bigmodel.cn](https://open.bigmodel.cn)
- **其他兼容OpenAI API格式的服务**

## 💾 数据库初始化

### 方法一：使用SQL脚本手动初始化

```bash
# 1. 连接MySQL
mysql -u root -p

# 2. 执行表结构脚本
source framework-ai-robot/sql/schema.sql

# 3. 执行初始化数据脚本
source framework-ai-robot/sql/data.sql
```

### 方法二：使用Docker自动初始化

如果使用项目的docker-compose，数据库会自动初始化：

```bash
# docker-compose会自动执行 docker/mysql/init/init.sql
docker-compose up -d mysql
```

### 数据库验证

```sql
-- 切换到ai_robot数据库
USE ai_robot;

-- 查看所有表
SHOW TABLES;

-- 查看机器人数据
SELECT * FROM ai_robot WHERE deleted = 0;
```

## ⚙️ 配置说明

### 1. 核心配置文件

**application.yml** - 主要配置项：

```yaml
server:
  port: 8083

spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/ai_robot
    username: root
    password: your_password
  
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      database: 1
  
  # Spring AI配置
  ai:
    openai:
      # API密钥（必填）
      api-key: ${SPRING_AI_OPENAI_API_KEY}
      # API基础URL（可选，默认为OpenAI官方地址）
      base-url: ${SPRING_AI_OPENAI_BASE_URL:https://api.openai.com}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 2000
```

### 2. 环境变量配置

创建 `.env` 文件（用于本地开发）：

```bash
# OpenAI配置
SPRING_AI_OPENAI_API_KEY=sk-your-api-key-here
SPRING_AI_OPENAI_BASE_URL=https://api.openai.com

# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ai_robot
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Redis配置
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

### 3. 不同AI服务商的配置示例

#### OpenAI官方

```yaml
spring:
  ai:
    openai:
      api-key: sk-xxxxxxxxxxxxxxxx
      base-url: https://api.openai.com
      chat:
        options:
          model: gpt-3.5-turbo
```

#### Azure OpenAI

```yaml
spring:
  ai:
    openai:
      api-key: your-azure-api-key
      base-url: https://your-resource.openai.azure.com/openai/deployments/your-deployment
      chat:
        options:
          model: gpt-35-turbo
```

#### 阿里云通义千问

```yaml
spring:
  ai:
    openai:
      api-key: sk-xxxxxxxxxxxxxxxx
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      chat:
        options:
          model: qwen-turbo
```

## 🏠 本地开发

### 1. 启动依赖服务

```bash
# 方法一：使用Docker启动依赖
docker-compose up -d mysql redis nacos

# 方法二：本地安装并启动服务
# 自行启动MySQL、Redis、Nacos
```

### 2. 配置环境变量

```bash
# Linux/Mac
export SPRING_AI_OPENAI_API_KEY=sk-your-api-key-here

# Windows PowerShell
$env:SPRING_AI_OPENAI_API_KEY="sk-your-api-key-here"

# Windows CMD
set SPRING_AI_OPENAI_API_KEY=sk-your-api-key-here
```

### 3. 启动服务

```bash
# 进入项目目录
cd framework-ai-robot

# Maven启动
mvn spring-boot:run

# 或者使用IDE（IDEA/Eclipse）直接运行AiRobotApplication
```

### 4. 验证服务

```bash
# 健康检查
curl http://localhost:8083/actuator/health

# 查看API文档
# 浏览器访问: http://localhost:8083/doc.html
```

## 🐳 Docker部署

### 1. 完整部署（推荐）

使用项目根目录的docker-compose：

```bash
# 设置环境变量
export OPENAI_API_KEY=sk-your-api-key-here
export OPENAI_BASE_URL=https://api.openai.com

# 启动所有服务
docker-compose up -d

# 查看AI机器人服务日志
docker-compose logs -f ai-robot

# 查看服务状态
docker-compose ps
```

### 2. 单独构建镜像

```bash
# 在项目根目录执行
docker build -f framework-ai-robot/Dockerfile -t my-framework-ai-robot:1.0.0 .

# 运行容器
docker run -d \
  --name ai-robot \
  -p 8083:8083 \
  -e SPRING_AI_OPENAI_API_KEY=sk-your-api-key \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ai_robot \
  -e SPRING_DATA_REDIS_HOST=redis \
  my-framework-ai-robot:1.0.0
```

### 3. Docker Compose配置示例

```yaml
services:
  ai-robot:
    build:
      context: .
      dockerfile: framework-ai-robot/Dockerfile
    container_name: my-framework-ai-robot
    restart: always
    ports:
      - "8083:8083"
    environment:
      # 基础配置
      SPRING_PROFILES_ACTIVE: docker
      
      # Nacos配置
      SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos:8848
      SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos:8848
      
      # 数据库配置
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ai_robot
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      
      # Redis配置
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      
      # OpenAI配置
      SPRING_AI_OPENAI_API_KEY: ${OPENAI_API_KEY}
      SPRING_AI_OPENAI_BASE_URL: ${OPENAI_BASE_URL:-https://api.openai.com}
    depends_on:
      - mysql
      - redis
      - nacos
    networks:
      - my-framework-network
```

## 🚀 生产环境部署

### 1. 准备工作

```bash
# 1. 编译打包
mvn clean package -pl framework-ai-robot -am -DskipTests

# 2. 检查jar包
ls -lh framework-ai-robot/target/framework-ai-robot-1.0.0.jar
```

### 2. 系统服务配置

创建systemd服务文件 `/etc/systemd/system/ai-robot.service`：

```ini
[Unit]
Description=AI Robot Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/my-framework
ExecStart=/usr/bin/java \
  -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  -jar /opt/my-framework/framework-ai-robot-1.0.0.jar

# 环境变量
Environment="SPRING_AI_OPENAI_API_KEY=sk-your-api-key"
Environment="SPRING_AI_OPENAI_BASE_URL=https://api.openai.com"

# 重启策略
Restart=always
RestartSec=10

# 日志
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
# 重载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start ai-robot

# 开机自启
sudo systemctl enable ai-robot

# 查看状态
sudo systemctl status ai-robot

# 查看日志
sudo journalctl -u ai-robot -f
```

### 3. Nginx反向代理配置

```nginx
upstream ai_robot_backend {
    server 127.0.0.1:8083;
    # 如果有多个实例，可以配置负载均衡
    # server 127.0.0.1:8084;
    # server 127.0.0.1:8085;
}

server {
    listen 80;
    server_name ai-robot.yourdomain.com;
    
    # HTTPS配置（推荐）
    # listen 443 ssl http2;
    # ssl_certificate /path/to/cert.pem;
    # ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://ai_robot_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket支持（如果需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 超时设置（AI响应可能较慢）
        proxy_connect_timeout 60s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;
    }
}
```

### 4. 监控和日志

#### Prometheus监控配置

```yaml
scrape_configs:
  - job_name: 'ai-robot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8083']
        labels:
          application: 'framework-ai-robot'
```

#### 日志收集

```bash
# 配置日志轮转 /etc/logrotate.d/ai-robot
/opt/my-framework/logs/framework-ai-robot.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 appuser appuser
    postrotate
        systemctl reload ai-robot > /dev/null 2>&1 || true
    endscript
}
```

## ❓ 常见问题

### 1. API调用失败

**问题**：AI调用返回401/403错误

**解决方案**：
- 检查API Key是否正确配置
- 确认API Key是否有效且有足够余额
- 检查base-url是否正确

### 2. 数据库连接失败

**问题**：启动时报数据库连接错误

**解决方案**：
```bash
# 检查数据库是否启动
mysql -u root -p -e "SHOW DATABASES;"

# 确认ai_robot数据库存在
mysql -u root -p -e "USE ai_robot; SHOW TABLES;"

# 检查连接配置
cat framework-ai-robot/src/main/resources/application.yml | grep datasource -A 5
```

### 3. 内存溢出

**问题**：服务运行一段时间后OOM

**解决方案**：
```bash
# 调整JVM参数
export JAVA_OPTS="-Xms1024m -Xmx2048m -XX:+UseG1GC"

# 监控内存使用
curl http://localhost:8083/actuator/metrics/jvm.memory.used
```

### 4. 响应超时

**问题**：AI响应时间过长

**解决方案**：
- 调整nginx proxy_read_timeout
- 优化提示词长度
- 减少max_tokens参数
- 使用更快的模型（如gpt-3.5-turbo）

### 5. Token使用量过大

**问题**：成本过高

**解决方案**：
- 限制历史消息数量（当前默认10条）
- 优化system_prompt长度
- 设置合理的max_tokens
- 添加请求频率限制

## 📞 技术支持

如有问题，请查看：
- 项目文档：`framework-ai-robot/README.md`
- API文档：http://localhost:8083/doc.html
- Spring AI文档：https://docs.spring.io/spring-ai/reference/

## 🔗 相关链接

- [主项目README](../../README.md)
- [服务README](../README.md)
- [数据库脚本](../sql/)
- [API使用指南](./API.md)

