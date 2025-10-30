# SkyWalking Agent 配置说明

## 📦 下载 Agent

在启动服务之前，需要先下载 SkyWalking Java Agent。

### Linux/Mac 系统
```bash
cd docker/skywalking
chmod +x download-agent.sh
./download-agent.sh
```

### Windows 系统
```cmd
cd docker\skywalking
download-agent.bat
```

## 📁 目录结构

下载完成后，agent 目录结构如下：
```
docker/skywalking/agent/
├── skywalking-agent.jar          # Agent 主程序
├── config/
│   └── agent.config              # Agent 配置文件
├── plugins/                      # 插件目录
├── optional-plugins/             # 可选插件
└── logs/                         # 日志目录
```

## ⚙️ 配置说明

主要配置项位于 `agent/config/agent.config` 文件中，关键配置：

```properties
# 服务名称（由环境变量 SW_AGENT_NAME 设置）
agent.service_name=${SW_AGENT_NAME:my-application}

# OAP 服务器地址（由环境变量 SW_AGENT_COLLECTOR_BACKEND_SERVICES 设置）
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}

# 日志级别
logging.level=${SW_LOGGING_LEVEL:INFO}

# 采样率（n 表示每 n 条追踪 1 条，-1 表示全部追踪）
agent.sample_n_per_3_secs=${SW_AGENT_SAMPLE:-1}
```

## 🚀 使用方式

### Docker Compose（推荐）
已在 `docker-compose.yml` 中配置好，直接启动即可：
```bash
docker-compose up -d
```

### 本地运行
如需本地运行服务并连接 SkyWalking：
```bash
java -javaagent:/path/to/skywalking-agent.jar \
     -DSW_AGENT_NAME=my-service \
     -DSW_AGENT_COLLECTOR_BACKEND_SERVICES=localhost:11800 \
     -jar your-application.jar
```

## 🔧 高级配置

### 启用可选插件

将需要的插件从 `optional-plugins/` 移动到 `plugins/` 目录：
```bash
# 例如启用 trace 日志插件
cp agent/optional-plugins/apm-trace-ignore-plugin-*.jar agent/plugins/
```

### 调整采样率

在生产环境可以降低采样率以减少性能开销：
```bash
# 设置环境变量
SW_AGENT_SAMPLE=1000  # 每 1000 条追踪 1 条
```

## 📝 注意事项

1. Agent 文件较大（约 40MB），首次下载需要时间
2. 确保 agent 目录权限正确（755）
3. 不要修改 skywalking-agent.jar 文件
4. 配置文件修改后需要重启服务

