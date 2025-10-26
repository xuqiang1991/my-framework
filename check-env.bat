@echo off
chcp 65001 >nul
echo ========================================
echo 环境检查
echo ========================================

echo.
echo 检查 Java...
where java >nul 2>&1
if %errorlevel% equ 0 (
    java -version 2>&1 | findstr "version"
    echo ✓ Java 已安装
) else (
    echo ✗ Java 未安装
    echo 请安装 JDK 21: https://www.oracle.com/java/technologies/downloads/#java21
)

echo.
echo 检查 Maven...
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    mvn -version 2>&1 | findstr "Apache Maven"
    echo ✓ Maven 已安装
) else (
    echo ✗ Maven 未安装
    echo 请安装 Maven: https://maven.apache.org/download.cgi
    echo 或使用 Chocolatey: choco install maven
)

echo.
echo 检查 Docker...
where docker >nul 2>&1
if %errorlevel% equ 0 (
    docker --version
    echo ✓ Docker 已安装
) else (
    echo ✗ Docker 未安装
    echo 请安装 Docker Desktop: https://www.docker.com/products/docker-desktop
)

echo.
echo 检查 Docker Compose...
where docker-compose >nul 2>&1
if %errorlevel% equ 0 (
    docker-compose --version
    echo ✓ Docker Compose 已安装
) else (
    echo ✗ Docker Compose 未安装
    echo Docker Desktop 自带 Docker Compose
)

echo.
echo ========================================
echo 检查完成
echo ========================================
echo.
echo 如果所有组件都已安装，可以运行:
echo   build.bat   - 编译项目
echo   start.bat   - 启动服务
echo.
echo 如果缺少组件，请查看 SETUP.md 获取详细安装说明
echo ========================================
pause


