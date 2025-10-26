# 依赖问题修复记录

## 已修复的问题

### 1. ✅ Redis 依赖缺失

**问题**: `程序包org.springframework.data.redis.core不存在`

**原因**: `framework-common` 模块中的 `RedisUtil` 和 `RedisConfig` 使用了 Spring Data Redis，但 POM 文件中缺少依赖。

**修复**: 在 `framework-common/pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <optional>true</optional>
</dependency>
```

### 2. ✅ Lombok 依赖缺失（framework-api）

**问题**: `com.myframework.api.auth.dto.LoginResponse程序包lombok不存在`

**原因**: `framework-api` 模块中的 DTO 类使用了 Lombok 注解（@Data, @Builder 等），但 POM 文件中缺少依赖。

**修复**: 在 `framework-api/pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 3. ✅ Lombok 依赖缺失（framework-gateway）

**问题**: `程序包lombok.extern.slf4j不存在 com.myframework.gateway.handler.GatewayExceptionHandler`

**原因**: `framework-gateway` 模块中使用了 `@Slf4j` 注解，但 POM 文件中缺少 Lombok 依赖。

**修复**: 在 `framework-gateway/pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 4. ✅ Lombok 依赖缺失（framework-auth）

**问题**: `framework-auth` 模块中的 `AuthService` 使用了 `@Slf4j` 注解

**原因**: 缺少 Lombok 依赖。

**修复**: 在 `framework-auth/pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 5. ✅ Lombok 依赖缺失（framework-user）

**问题**: `framework-user` 模块中的实体类和服务类使用了 Lombok 注解

**原因**: 缺少 Lombok 依赖。

**修复**: 在 `framework-user/pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

## 依赖问题检查清单

### framework-common 模块
- ✅ spring-boot-starter-web
- ✅ spring-boot-starter-validation
- ✅ spring-boot-starter-data-redis
- ✅ lombok
- ✅ hutool-all
- ✅ jjwt-api
- ✅ jackson-databind

### framework-api 模块
- ✅ framework-common
- ✅ spring-cloud-starter-openfeign
- ✅ spring-cloud-starter-loadbalancer
- ✅ lombok

### framework-gateway 模块
- ✅ framework-common
- ✅ spring-cloud-starter-gateway
- ✅ spring-cloud-starter-alibaba-nacos-discovery
- ✅ spring-cloud-starter-alibaba-nacos-config
- ✅ sa-token-reactor-spring-boot3-starter
- ✅ spring-boot-starter-data-redis-reactive
- ✅ spring-cloud-starter-alibaba-sentinel
- ✅ lombok

### framework-auth 模块
- ✅ framework-common
- ✅ framework-api
- ✅ spring-boot-starter-web
- ✅ spring-cloud-starter-alibaba-nacos-discovery
- ✅ spring-cloud-starter-openfeign
- ✅ sa-token-spring-boot3-starter
- ✅ spring-boot-starter-data-redis
- ✅ knife4j-openapi3-jakarta-spring-boot-starter
- ✅ lombok

### framework-user 模块
- ✅ framework-common
- ✅ framework-api
- ✅ spring-boot-starter-web
- ✅ mysql-connector-j
- ✅ mybatis-plus-boot-starter
- ✅ druid-spring-boot-3-starter
- ✅ spring-boot-starter-data-redis
- ✅ spring-cloud-starter-alibaba-sentinel
- ✅ lombok

## 验证修复

### 使用 Maven 验证（推荐）

```bash
# 清理并编译所有模块
mvn clean compile

# 如果编译成功，会看到 BUILD SUCCESS
```

### 使用 IDE 验证

**IntelliJ IDEA**:
1. 右键点击根目录的 `pom.xml`
2. 选择 Maven → Reload Project
3. 等待依赖下载完成
4. 选择 Maven → Compile
5. 查看 Build 窗口，应该显示 "BUILD SUCCESS"

**Eclipse**:
1. 右键点击项目
2. 选择 Maven → Update Project
3. 勾选 "Force Update of Snapshots/Releases"
4. 点击 OK
5. 右键项目 → Run As → Maven build
6. Goals 输入: clean compile
7. 点击 Run

### 检查编译结果

编译成功后，以下目录应该包含 .class 文件：
- `framework-common/target/classes/`
- `framework-api/target/classes/`
- `framework-gateway/target/classes/`
- `framework-auth/target/classes/`
- `framework-user/target/classes/`

## 常见依赖问题

### 1. 依赖下载失败

**症状**: "Could not resolve dependencies"

**解决方案**:
```bash
# 方案1: 配置阿里云镜像（见 SETUP.md）

# 方案2: 清理本地仓库重新下载
# Windows
rmdir /s %USERPROFILE%\.m2\repository
mvn clean install

# Linux/Mac
rm -rf ~/.m2/repository
mvn clean install
```

### 2. 依赖冲突

**症状**: "Dependency convergence error"

**解决方案**:
```bash
# 查看依赖树
mvn dependency:tree

# 排除冲突的依赖
<dependency>
    <groupId>xxx</groupId>
    <artifactId>xxx</artifactId>
    <exclusions>
        <exclusion>
            <groupId>conflict-group</groupId>
            <artifactId>conflict-artifact</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 3. 版本不兼容

**症状**: "NoSuchMethodError" 或 "ClassNotFoundException"

**解决方案**:
- 检查父 POM 中的版本定义
- 确保所有依赖版本兼容
- 参考 Spring Cloud Alibaba 官方版本对应关系

### 4. Scope 设置错误

**注意**:
- `provided`: 编译时需要，运行时由容器提供（如 Lombok）
- `runtime`: 运行时需要，编译时不需要（如 JDBC 驱动）
- `compile`: 编译和运行时都需要（默认）
- `test`: 仅测试时需要

## 完整的依赖版本

当前项目使用的版本（参考父 POM）:

```xml
<!-- Spring Boot & Cloud -->
<spring-boot.version>3.2.0</spring-boot.version>
<spring-cloud.version>2023.0.0</spring-cloud.version>
<spring-cloud-alibaba.version>2023.0.1.0</spring-cloud-alibaba.version>

<!-- Database -->
<mysql.version>8.0.33</mysql.version>
<mybatis-plus.version>3.5.5</mybatis-plus.version>
<druid.version>1.2.20</druid.version>

<!-- Cache -->
<redisson.version>3.25.2</redisson.version>

<!-- Security -->
<sa-token.version>1.37.0</sa-token.version>
<jjwt.version>0.12.3</jjwt.version>

<!-- Tools -->
<hutool.version>5.8.23</hutool.version>
<lombok.version>1.18.30</lombok.version>
<knife4j.version>4.4.0</knife4j.version>
```

## 下载依赖技巧

### 加速依赖下载

**配置阿里云镜像** (推荐):

编辑 `~/.m2/settings.xml` (Windows: `%USERPROFILE%\.m2\settings.xml`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <!-- 阿里云 Maven 中央仓库 -->
        <mirror>
            <id>aliyun-central</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Central</name>
            <url>https://maven.aliyun.com/repository/central</url>
        </mirror>
        
        <!-- 阿里云 Maven 公共仓库 -->
        <mirror>
            <id>aliyun-public</id>
            <mirrorOf>public</mirrorOf>
            <name>Aliyun Public</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
        
        <!-- 阿里云 Spring 仓库 -->
        <mirror>
            <id>aliyun-spring</id>
            <mirrorOf>spring</mirrorOf>
            <name>Aliyun Spring</name>
            <url>https://maven.aliyun.com/repository/spring</url>
        </mirror>
    </mirrors>
</settings>
```

### 离线模式

如果已经下载了所有依赖：
```bash
mvn clean compile -o  # -o 表示 offline
```

### 并行下载

在 `~/.m2/settings.xml` 中配置：
```xml
<settings>
    <profiles>
        <profile>
            <id>maven-parallel</id>
            <properties>
                <maven.artifact.threads>10</maven.artifact.threads>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>maven-parallel</activeProfile>
    </activeProfiles>
</settings>
```

## 完整编译命令

```bash
# 基础编译
mvn clean compile

# 跳过测试编译
mvn clean compile -DskipTests

# 编译并打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 安装到本地仓库
mvn clean install

# 并行编译（加速）
mvn clean compile -T 4  # 使用4个线程

# 调试模式（查看详细信息）
mvn clean compile -X

# 离线模式
mvn clean compile -o
```

## 验证所有模块

```bash
# 编译所有模块
mvn clean compile

# 预期输出：
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for my-framework 1.0.0:
[INFO] 
[INFO] my-framework ....................................... SUCCESS
[INFO] Framework Common ................................... SUCCESS
[INFO] Framework API ...................................... SUCCESS
[INFO] Framework Gateway .................................. SUCCESS
[INFO] Framework Auth ..................................... SUCCESS
[INFO] Framework User ..................................... SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## 遇到问题？

1. **查看完整错误信息**
   ```bash
   mvn clean compile -X  # 详细模式
   ```

2. **清理并重新编译**
   ```bash
   mvn clean
   mvn compile
   ```

3. **删除有问题的依赖，重新下载**
   ```bash
   # 删除本地仓库中的某个依赖
   # Windows
   rmdir /s %USERPROFILE%\.m2\repository\org\springframework\boot
   
   # Linux/Mac
   rm -rf ~/.m2/repository/org/springframework/boot
   ```

4. **使用 IDE 重新导入**
   - IntelliJ IDEA: File → Invalidate Caches / Restart
   - Eclipse: 右键项目 → Maven → Update Project → Force Update

## 更新记录

- **2024-10-23 17:00**: 修复 Redis 依赖缺失问题（framework-common）
- **2024-10-23 17:05**: 修复 Lombok 依赖缺失问题（framework-api）
- **2024-10-23 17:10**: 修复 Lombok 依赖缺失问题（framework-gateway）
- **2024-10-23 17:15**: 修复 Lombok 依赖缺失问题（framework-auth）
- **2024-10-23 17:15**: 修复 Lombok 依赖缺失问题（framework-user）
- **2024-10-23 17:20**: 完善依赖配置，所有模块可以正常编译 ✅

## 相关文档

- [SETUP.md](SETUP.md) - 环境配置指南
- [README.md](README.md) - 项目概述
- [DEPLOY.md](DEPLOY.md) - 部署指南

