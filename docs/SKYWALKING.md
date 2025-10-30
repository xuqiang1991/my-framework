# 🔍 SkyWalking 分布式追踪使用指南

## 📖 简介

SkyWalking 是一个开源的 APM（Application Performance Monitoring）系统，专为微服务、云原生和容器化架构设计。它提供分布式追踪、服务网格遥测分析、度量聚合和可视化一体化解决方案。

### ✨ 核心功能

- 🔗 **分布式追踪**：追踪跨服务的请求调用链路
- 📊 **性能分析**：监控服务响应时间、吞吐量、错误率
- 🗺️ **服务拓扑**：自动生成服务依赖关系图
- 📈 **指标监控**：JVM、数据库、缓存等多维度监控
- ⚠️ **告警功能**：支持自定义告警规则
- 📝 **日志关联**：将日志与追踪数据关联

---

## 🚀 快速开始

### 1. 下载 SkyWalking Agent

在启动服务之前，需要先下载 SkyWalking Java Agent。

#### Linux/Mac 系统
```bash
cd docker/skywalking
chmod +x download-agent.sh
./download-agent.sh
```

#### Windows 系统
```cmd
cd docker\skywalking
download-agent.bat
```

### 2. 启动所有服务

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 3. 访问 SkyWalking UI

打开浏览器访问：http://localhost:8088

**首次访问可能需要等待 1-2 分钟，等待服务启动和数据采集。**

---

## 🎯 功能使用

### 1. 服务拓扑图

**路径**：Dashboard → Topology

- 查看所有服务之间的调用关系
- 实时显示服务健康状态
- 点击服务节点查看详细信息

**示例**：
```
[网关服务] → [认证服务]
            → [用户服务]
```

### 2. 追踪查询

**路径**：Trace → Trace

#### 查看调用链路
1. 选择时间范围
2. 选择服务（如：user-service）
3. 点击具体的 Trace ID 查看详情

#### 追踪详情包含
- 每个服务的调用时间
- 请求参数和响应信息
- 异常堆栈（如果有）
- SQL 语句执行情况
- Redis 操作记录

### 3. 服务性能监控

**路径**：Dashboard → Service

#### 关键指标
- **SLA（服务等级协议）**：服务可用性百分比
- **CPM（每分钟调用次数）**：服务吞吐量
- **Avg Response Time**：平均响应时间
- **Apdex Score**：用户满意度评分

#### 查看方式
```
1. 选择服务：user-service
2. 选择时间范围：Last 15 minutes
3. 查看各项指标趋势图
```

### 4. 端点（Endpoint）分析

**路径**：Dashboard → Endpoint

查看具体 API 接口的性能：
- `/oauth2/token` - Token 获取接口
- `/user/register/self` - 用户注册接口
- `/auth/info` - 获取用户信息接口

### 5. 数据库监控

**路径**：Dashboard → Database

查看数据库操作：
- SQL 执行时间
- 慢查询统计
- 数据库连接池状态

### 6. 日志查看

**路径**：Log → Logs

查看服务日志：
- 关联到具体的 Trace
- 支持日志级别过滤
- 支持关键字搜索

---

## 🔧 配置说明

### 服务配置

每个服务的 SkyWalking 配置在 `docker-compose.yml` 中：

```yaml
environment:
  SW_AGENT_NAME: user-service              # 服务名称
  SW_AGENT_COLLECTOR_BACKEND_SERVICES: skywalking-oap:11800  # OAP 地址
  JAVA_TOOL_OPTIONS: "-javaagent:/skywalking/agent/skywalking-agent.jar"
volumes:
  - ./docker/skywalking/agent:/skywalking/agent
```

### Agent 配置

配置文件位置：`docker/skywalking/agent/config/agent.config`

#### 常用配置项

```properties
# 服务名称
agent.service_name=${SW_AGENT_NAME:my-application}

# OAP 服务器地址
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}

# 采样率（-1 表示全部采样，n 表示每 n 条采样 1 条）
agent.sample_n_per_3_secs=${SW_AGENT_SAMPLE:-1}

# 日志级别：DEBUG, INFO, WARN, ERROR
logging.level=${SW_LOGGING_LEVEL:INFO}

# 日志输出
logging.output=${SW_LOGGING_OUTPUT:FILE}

# 忽略指定后缀的追踪
agent.ignore_suffix=${SW_AGENT_IGNORE_SUFFIX:.jpg,.jpeg,.js,.css,.png,.bmp,.gif,.ico,.mp3,.mp4,.html,.svg}

# 限制追踪的最大 Span 数量
agent.span_limit_per_segment=${SW_AGENT_SPAN_LIMIT:300}
```

### 性能调优

#### 生产环境配置建议

```yaml
environment:
  # 降低采样率以减少性能开销
  SW_AGENT_SAMPLE: 1000  # 每 1000 条追踪 1 条
  
  # 调整日志级别
  SW_LOGGING_LEVEL: WARN
  
  # 限制 Span 数量
  SW_AGENT_SPAN_LIMIT: 150
```

#### 开发环境配置

```yaml
environment:
  # 全量采样以便调试
  SW_AGENT_SAMPLE: -1
  
  # 详细日志
  SW_LOGGING_LEVEL: DEBUG
```

---

## 📊 服务地址

| 服务 | 地址 | 说明 |
|------|------|------|
| SkyWalking UI | http://localhost:8088 | 可视化界面 |
| SkyWalking OAP | http://localhost:12800 | HTTP API |
| SkyWalking OAP gRPC | localhost:11800 | Agent 数据上报 |
| Elasticsearch | http://localhost:9200 | 存储后端 |

---

## 🔍 实战案例

### 案例1：追踪用户注册流程

1. **发起注册请求**
```bash
curl -X POST http://localhost:8081/user/register/self \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "nickname": "测试用户"
  }'
```

2. **在 SkyWalking UI 中查看**
   - 访问 Trace → Trace
   - 搜索 `/user/register/self`
   - 点击 Trace ID 查看详情

3. **分析调用链路**
```
Gateway Service
  └─> Auth Service (验证请求)
       └─> User Service (创建用户)
            ├─> MySQL (插入用户数据)
            └─> Redis (缓存用户信息)
```

### 案例2：分析慢查询

1. **查看数据库监控**
   - Dashboard → Database
   - 选择 MySQL
   - 查看 "Top 10 of Database Slow Statement"

2. **定位问题 SQL**
   - 点击慢查询语句
   - 查看执行时间分布
   - 找到对应的 Trace

3. **优化建议**
   - 添加索引
   - 优化查询语句
   - 使用缓存

### 案例3：OAuth2 授权流程追踪

1. **完整授权流程**
```bash
# 1. 获取授权码
GET /oauth2/authorize?response_type=code&client_id=main-app-client&...

# 2. 换取 Token
POST /oauth2/token
```

2. **查看追踪**
   - 搜索 `/oauth2/authorize`
   - 查看跨服务调用
   - 分析性能瓶颈

### 案例4：异常追踪

1. **制造一个异常**
```bash
# 使用无效的 Token 访问接口
curl http://localhost:8081/auth/info \
  -H "Authorization: Bearer invalid-token"
```

2. **在 SkyWalking 中查看**
   - Trace → Trace
   - 筛选条件：Status = Error
   - 查看异常堆栈和错误信息

---

## ⚙️ 高级功能

### 1. 自定义告警

编辑 `docker/skywalking/config/alarm-settings.yml`：

```yaml
rules:
  # 服务响应时间告警
  service_resp_time_rule:
    metrics-name: service_resp_time
    op: ">"
    threshold: 1000  # 超过 1 秒告警
    period: 10
    count: 3
    message: 服务 {name} 响应时间超过 1 秒
```

### 2. 自定义追踪

在代码中添加自定义 Span：

```java
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Service
public class UserService {
    
    @Trace
    public void processUser(User user) {
        // 添加自定义标签
        ActiveSpan.tag("userId", user.getId());
        ActiveSpan.tag("action", "process");
        
        // 业务逻辑
        // ...
    }
}
```

### 3. 日志关联

在 `logback-spring.xml` 中配置：

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%tid] [%thread] %-5level %logger{36} - %msg%n</pattern>
        </layout>
    </encoder>
</appender>
```

### 4. 性能分析

使用 SkyWalking 的性能剖析功能：
1. Dashboard → Profile
2. 创建新的 Profile 任务
3. 选择服务和端点
4. 设置采样参数
5. 查看方法级别的性能数据

---

## 📝 最佳实践

### 1. 命名规范

```yaml
# 服务命名：使用有意义的名称
SW_AGENT_NAME: user-service          # ✅ 好
SW_AGENT_NAME: service1              # ❌ 差

# 保持一致性
SW_AGENT_NAME: ${SERVICE_NAME}       # 使用环境变量
```

### 2. 采样策略

```yaml
# 开发环境：全量采样
SW_AGENT_SAMPLE: -1

# 测试环境：高频采样
SW_AGENT_SAMPLE: 100

# 生产环境：根据流量调整
SW_AGENT_SAMPLE: 1000  # 大流量
SW_AGENT_SAMPLE: 500   # 中流量
SW_AGENT_SAMPLE: -1    # 小流量
```

### 3. 监控重点

- ✅ 关键业务接口（登录、支付、下单）
- ✅ 跨服务调用
- ✅ 数据库操作
- ✅ 缓存操作
- ✅ 第三方 API 调用

### 4. 性能优化

```properties
# 减少追踪数据大小
agent.span_limit_per_segment=150

# 忽略静态资源
agent.ignore_suffix=.jpg,.jpeg,.js,.css,.png

# 异步批量发送
plugin.peer_max_length=200
```

---

## 🐛 故障排查

### 问题1：UI 无法访问

**症状**：http://localhost:8088 无法打开

**解决方案**：
```bash
# 检查服务状态
docker-compose ps

# 查看日志
docker-compose logs skywalking-ui
docker-compose logs skywalking-oap

# 等待服务完全启动（约 2-3 分钟）
```

### 问题2：没有追踪数据

**症状**：UI 可以访问，但是没有任何追踪数据

**检查步骤**：
1. 确认 Agent 已正确挂载
```bash
docker exec my-framework-user ls -la /skywalking/agent
```

2. 检查服务日志
```bash
docker-compose logs user
# 应该看到 "SkyWalking agent initialized" 类似的日志
```

3. 确认环境变量
```bash
docker exec my-framework-user env | grep SW_
```

4. 检查 OAP 连接
```bash
# 查看 OAP 日志
docker-compose logs skywalking-oap | grep "segment"
```

### 问题3：Agent 下载失败

**症状**：执行 download-agent.sh 失败

**解决方案**：
```bash
# 方法1：手动下载
wget https://archive.apache.org/dist/skywalking/9.7.0/apache-skywalking-java-agent-9.7.0.tgz
tar -zxf apache-skywalking-java-agent-9.7.0.tgz
mv skywalking-agent docker/skywalking/agent

# 方法2：使用国内镜像
wget https://mirrors.tuna.tsinghua.edu.cn/apache/skywalking/9.7.0/apache-skywalking-java-agent-9.7.0.tgz
```

### 问题4：Elasticsearch 启动失败

**症状**：elasticsearch 容器反复重启

**解决方案**：
```bash
# 增加虚拟内存
# Linux
sudo sysctl -w vm.max_map_count=262144

# Windows (WSL2)
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
```

### 问题5：性能影响

**症状**：接入 SkyWalking 后服务变慢

**优化方案**：
```yaml
# 降低采样率
SW_AGENT_SAMPLE: 1000

# 限制 Span 数量
SW_AGENT_SPAN_LIMIT: 150

# 禁用不需要的插件
# 移除 agent/plugins/ 中不需要的插件
```

---

## 📚 参考资料

- [SkyWalking 官方文档](https://skywalking.apache.org/docs/)
- [SkyWalking Java Agent](https://github.com/apache/skywalking-java)
- [SkyWalking UI 使用指南](https://skywalking.apache.org/docs/main/latest/en/ui/readme/)

---

## 🎯 总结

### SkyWalking 的价值

1. **提升问题定位效率**：快速找到性能瓶颈和错误源头
2. **优化系统性能**：通过数据分析找到优化点
3. **保障服务质量**：实时监控服务健康状态
4. **降低运维成本**：自动化的追踪和告警

### 下一步

1. ✅ 完成 SkyWalking 环境搭建
2. ⏳ 熟悉 UI 各项功能
3. ⏳ 配置自定义告警规则
4. ⏳ 集成到 CI/CD 流程
5. ⏳ 建立性能基线和 SLA

---

**🎉 现在你已经掌握了 SkyWalking 的使用，开始享受分布式追踪带来的便利吧！**

