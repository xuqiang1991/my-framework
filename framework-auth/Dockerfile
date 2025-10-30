# 第一阶段：使用 Maven 编译项目
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# 设置工作目录
WORKDIR /build

# 配置阿里云 Maven 镜像加速（国内网络优化）
RUN mkdir -p /root/.m2 && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
          https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
      <mirrors> \
        <mirror> \
          <id>aliyun</id> \
          <mirrorOf>central</mirrorOf> \
          <name>Aliyun Maven Mirror</name> \
          <url>https://maven.aliyun.com/repository/public</url> \
        </mirror> \
      </mirrors> \
    </settings>' > /root/.m2/settings.xml

# 复制父 pom.xml
COPY pom.xml .

# 复制所有模块的 pom.xml（用于依赖下载）
COPY framework-common/pom.xml framework-common/
COPY framework-api/pom.xml framework-api/
COPY framework-gateway/pom.xml framework-gateway/
COPY framework-auth/pom.xml framework-auth/
COPY framework-user/pom.xml framework-user/

# 下载依赖（利用 BuildKit 缓存挂载，依赖会被缓存到宿主机）
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B -pl framework-auth -am

# 复制所有源代码
COPY framework-common/src framework-common/src
COPY framework-api/src framework-api/src
COPY framework-auth/src framework-auth/src

# 编译项目（使用缓存挂载）
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl framework-auth -am -DskipTests

# 第二阶段：运行镜像
FROM eclipse-temurin:21-jre-alpine

# 安装 curl（用于健康检查）
RUN apk add --no-cache curl

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/framework-auth/target/framework-auth-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8081

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx512m"

# SkyWalking Agent 将通过 volume 挂载，无需在镜像中包含
# 启动命令会由 JAVA_TOOL_OPTIONS 环境变量自动添加 -javaagent 参数

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
