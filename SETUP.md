# 环境配置指南

## 问题：程序包org.springframework.data.redis.core不存在

这个问题已经修复！我已经在 `framework-common/pom.xml` 中添加了 Redis 依赖。

## 环境要求

### 必需软件

1. **JDK 21**
2. **Maven 3.8+**
3. **Docker Desktop**
4. **Git**

## 快速安装

### Windows 系统

#### 方式一：使用 Chocolatey（推荐）

```powershell
# 以管理员身份运行PowerShell

# 安装Chocolatey（如果还没安装）
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 安装JDK 21
choco install openjdk --version=21.0.0

# 安装Maven
choco install maven

# 安装Docker Desktop
choco install docker-desktop

# 验证安装
java -version
mvn -version
docker --version
```

#### 方式二：手动安装

**1. 安装 JDK 21**

1. 下载：https://www.oracle.com/java/technologies/downloads/#java21
2. 运行安装程序
3. 配置环境变量：
   ```
   JAVA_HOME = C:\Program Files\Java\jdk-21
   Path 添加: %JAVA_HOME%\bin
   ```
4. 验证：打开新的命令行窗口，运行 `java -version`

**2. 安装 Maven**

1. 下载：https://maven.apache.org/download.cgi
2. 解压到：`C:\Program Files\Apache\maven`
3. 配置环境变量：
   ```
   MAVEN_HOME = C:\Program Files\Apache\maven
   Path 添加: %MAVEN_HOME%\bin
   ```
4. 验证：打开新的命令行窗口，运行 `mvn -version`

**3. 安装 Docker Desktop**

1. 下载：https://www.docker.com/products/docker-desktop
2. 运行安装程序
3. 启动 Docker Desktop
4. 验证：`docker --version`

### Linux 系统

#### Ubuntu/Debian

```bash
# 更新包列表
sudo apt update

# 安装JDK 21
sudo apt install openjdk-21-jdk

# 安装Maven
sudo apt install maven

# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo apt install docker-compose

# 验证
java -version
mvn -version
docker --version
docker-compose --version
```

#### CentOS/RHEL

```bash
# 安装JDK 21
sudo yum install java-21-openjdk-devel

# 安装Maven
sudo yum install maven

# 安装Docker
sudo yum install docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证
java -version
mvn -version
docker --version
docker-compose --version
```

### macOS 系统

```bash
# 安装Homebrew（如果还没安装）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装JDK 21
brew install openjdk@21

# 链接JDK
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# 安装Maven
brew install maven

# 安装Docker Desktop
brew install --cask docker

# 验证
java -version
mvn -version
docker --version
docker-compose --version
```

## 配置 Maven 镜像（可选，提升下载速度）

编辑 Maven 配置文件（Windows: `%USERPROFILE%\.m2\settings.xml`, Linux/Mac: `~/.m2/settings.xml`）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <!-- 阿里云Maven镜像 -->
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Mirror</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

## 使用 IDE 开发（推荐）

如果不想手动安装 Maven，可以使用 IDE 内置的 Maven：

### IntelliJ IDEA

1. **下载安装 IntelliJ IDEA**
   - 社区版（免费）：https://www.jetbrains.com/idea/download/
   - 旗舰版（30天试用）：包含更多企业级功能

2. **打开项目**
   - File -> Open -> 选择 `my-framework` 目录
   - IDEA 会自动识别为 Maven 项目

3. **配置 JDK**
   - File -> Project Structure -> Project
   - SDK: 选择 JDK 21
   - Language Level: 21

4. **刷新 Maven**
   - 右侧 Maven 面板 -> 点击刷新图标
   - 等待依赖下载完成

5. **编译项目**
   - Maven 面板 -> Lifecycle -> clean
   - Maven 面板 -> Lifecycle -> compile

6. **运行服务**
   - 找到各服务的 Application 类
   - 右键 -> Run 'XXXApplication'

### Visual Studio Code

1. **下载安装 VSCode**
   - https://code.visualstudio.com/

2. **安装插件**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Docker

3. **打开项目**
   - File -> Open Folder -> 选择 `my-framework`

4. **配置 JDK**
   - Ctrl+Shift+P -> Java: Configure Java Runtime
   - 选择 JDK 21

5. **运行服务**
   - 在各服务的 Application.java 文件中
   - 点击 Run 按钮

## 环境检查脚本

### Windows (PowerShell)

创建文件 `check-env.ps1`：

```powershell
Write-Host "========================================" -ForegroundColor Green
Write-Host "环境检查" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 检查Java
Write-Host "`n检查 Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✓ $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java 未安装" -ForegroundColor Red
}

# 检查Maven
Write-Host "`n检查 Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✓ $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven 未安装" -ForegroundColor Red
}

# 检查Docker
Write-Host "`n检查 Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    Write-Host "✓ $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker 未安装" -ForegroundColor Red
}

# 检查Docker Compose
Write-Host "`n检查 Docker Compose..." -ForegroundColor Yellow
try {
    $composeVersion = docker-compose --version
    Write-Host "✓ $composeVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker Compose 未安装" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "检查完成" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
```

运行：`powershell -ExecutionPolicy Bypass -File check-env.ps1`

### Linux/Mac (Bash)

创建文件 `check-env.sh`：

```bash
#!/bin/bash

echo "========================================"
echo "环境检查"
echo "========================================"

# 检查Java
echo ""
echo "检查 Java..."
if command -v java &> /dev/null; then
    java -version 2>&1 | head -n 1
    echo "✓ Java 已安装"
else
    echo "✗ Java 未安装"
fi

# 检查Maven
echo ""
echo "检查 Maven..."
if command -v mvn &> /dev/null; then
    mvn -version 2>&1 | head -n 1
    echo "✓ Maven 已安装"
else
    echo "✗ Maven 未安装"
fi

# 检查Docker
echo ""
echo "检查 Docker..."
if command -v docker &> /dev/null; then
    docker --version
    echo "✓ Docker 已安装"
else
    echo "✗ Docker 未安装"
fi

# 检查Docker Compose
echo ""
echo "检查 Docker Compose..."
if command -v docker-compose &> /dev/null; then
    docker-compose --version
    echo "✓ Docker Compose 已安装"
else
    echo "✗ Docker Compose 未安装"
fi

echo ""
echo "========================================"
echo "检查完成"
echo "========================================"
```

运行：`chmod +x check-env.sh && ./check-env.sh`

## 常见问题

### 1. Maven 下载依赖很慢

**解决方案**：配置阿里云镜像（见上文）

### 2. Docker Desktop 启动失败

**Windows**：
- 确保已启用 WSL 2
- 确保已启用虚拟化（BIOS中）

**Linux**：
- 确保用户在 docker 组中：`sudo usermod -aG docker $USER`
- 重新登录或重启

### 3. 端口被占用

**检查端口**：
```bash
# Windows
netstat -ano | findstr "8080"

# Linux/Mac
lsof -i :8080
```

**释放端口**：
- 停止占用端口的程序
- 或修改 `application.yml` 中的端口号

### 4. 编译错误

**清理并重新编译**：
```bash
mvn clean install -DskipTests
```

**删除本地仓库缓存**：
```bash
# Windows
rmdir /s %USERPROFILE%\.m2\repository

# Linux/Mac
rm -rf ~/.m2/repository

# 然后重新编译
mvn clean install -DskipTests
```

## 下一步

环境配置完成后：

1. **编译项目**
   ```bash
   # Windows
   build.bat
   
   # Linux/Mac
   ./build.sh
   ```

2. **启动服务**
   ```bash
   # Windows
   start.bat
   
   # Linux/Mac
   ./start.sh
   ```

3. **验证服务**
   - Nacos: http://localhost:8848/nacos
   - Gateway: http://localhost:8080
   - API文档: http://localhost:8081/doc.html

## 获取帮助

- 查看 `README.md` - 项目概述
- 查看 `DEPLOY.md` - 部署指南
- 查看 `API.md` - API文档
- 提交 Issue - 报告问题


