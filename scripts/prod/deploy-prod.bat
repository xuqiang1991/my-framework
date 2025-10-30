@echo off
REM 生产环境部署脚本 (Windows)

setlocal enabledelayedexpansion

echo ==========================================
echo 生产环境部署脚本
echo ==========================================
echo.

REM 检查配置文件
if not exist ".env.prod" (
    echo ❌ 错误：未找到 .env.prod 配置文件
    echo 请先创建 .env.prod 文件并配置生产环境参数
    pause
    exit /b 1
)

echo 📋 环境检查...
REM 检查 Docker
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Docker 未安装
    pause
    exit /b 1
)

REM 检查 Docker Compose
where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Docker Compose 未安装
    pause
    exit /b 1
)

echo ✅ 环境检查通过
echo.

REM 停止旧服务
echo 🛑 停止旧服务...
docker-compose -f docker-compose.prod.yml down

REM 备份数据
echo 💾 备份数据...
set BACKUP_DIR=backups\%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_DIR=%BACKUP_DIR: =0%
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
if exist "docker\mysql\data" (
    xcopy /E /I /Y docker\mysql\data "%BACKUP_DIR%\mysql" >nul
    echo ✅ MySQL 数据已备份到 %BACKUP_DIR%\mysql
)

REM 构建镜像
echo 🔨 构建生产环境镜像...
docker-compose -f docker-compose.prod.yml build

REM 启动服务
echo 🚀 启动生产环境服务...
docker-compose -f docker-compose.prod.yml up -d

REM 等待服务启动
echo ⏳ 等待服务启动...
timeout /t 30 /nobreak >nul

REM 健康检查
echo 🏥 执行健康检查...
call scripts\prod\health-check.bat

echo.
echo ==========================================
echo ✅ 生产环境部署完成！
echo ==========================================
echo 📊 查看服务状态: docker-compose -f docker-compose.prod.yml ps
echo 📝 查看日志: docker-compose -f docker-compose.prod.yml logs -f [service]
echo.

pause

