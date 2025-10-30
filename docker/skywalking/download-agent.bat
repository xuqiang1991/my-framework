@echo off
REM SkyWalking Agent 下载脚本 (Windows 版本)
REM 用于下载并配置 SkyWalking Java Agent

setlocal enabledelayedexpansion

set SKYWALKING_VERSION=9.7.0
set AGENT_DIR=docker\skywalking\agent
set DOWNLOAD_URL=https://archive.apache.org/dist/skywalking/%SKYWALKING_VERSION%/apache-skywalking-java-agent-%SKYWALKING_VERSION%.tgz

echo ==========================================
echo SkyWalking Agent 下载脚本
echo 版本: %SKYWALKING_VERSION%
echo ==========================================
echo.

REM 检查 PowerShell 是否可用
where powershell >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ PowerShell 未安装，请手动下载 Agent
    echo 下载地址: %DOWNLOAD_URL%
    pause
    exit /b 1
)

REM 检查 agent 目录是否已存在
if exist "%AGENT_DIR%" (
    echo ⚠️  Agent 目录已存在: %AGENT_DIR%
    set /p CONFIRM="是否重新下载？(y/n): "
    if /i not "!CONFIRM!"=="y" (
        echo 跳过下载
        exit /b 0
    )
    rmdir /s /q "%AGENT_DIR%"
)

REM 创建临时目录
set TMP_DIR=%TEMP%\skywalking-agent-%RANDOM%
mkdir "%TMP_DIR%"
echo 📁 创建临时目录: %TMP_DIR%

REM 下载文件
echo 📥 下载 SkyWalking Agent...
echo 这可能需要几分钟，请耐心等待...
echo.

powershell -Command "& { $ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%TMP_DIR%\agent.tgz' }"

if %errorlevel% neq 0 (
    echo ❌ 下载失败，请检查网络连接
    rmdir /s /q "%TMP_DIR%"
    pause
    exit /b 1
)

REM 解压文件 (使用 tar 命令，Windows 10+ 自带)
echo 📦 解压文件...
tar -xzf "%TMP_DIR%\agent.tgz" -C "%TMP_DIR%"

if %errorlevel% neq 0 (
    echo ❌ 解压失败，请确保已安装 tar 工具
    echo 或手动解压下载的文件到: %AGENT_DIR%
    rmdir /s /q "%TMP_DIR%"
    pause
    exit /b 1
)

REM 移动到目标目录
echo 📂 安装到目标目录...
if not exist "docker\skywalking" mkdir "docker\skywalking"
move "%TMP_DIR%\skywalking-agent" "%AGENT_DIR%"

REM 清理临时文件
echo 🧹 清理临时文件...
rmdir /s /q "%TMP_DIR%"

echo.
echo ✅ SkyWalking Agent 安装成功！
echo 📍 安装路径: %AGENT_DIR%
echo 📝 配置文件: %AGENT_DIR%\config\agent.config
echo.
echo 下一步：
echo 1. 查看配置文件并根据需要调整
echo 2. 运行 docker-compose up 启动服务
echo.

pause

