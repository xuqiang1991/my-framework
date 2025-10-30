@echo off
chcp 65001 >nul
echo ======================================
echo 开始构建微服务项目
echo ======================================

echo 1. 清理并编译Maven项目...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo Maven构建失败！
    pause
    exit /b 1
)

echo ======================================
echo Maven构建完成！
echo ======================================

echo 2. 构建Docker镜像...

echo 构建网关服务镜像...
cd framework-gateway
docker build -t my-framework/gateway:1.0.0 .
cd ..

echo 构建认证服务镜像...
cd framework-auth
docker build -t my-framework/auth:1.0.0 .
cd ..

echo 构建用户服务镜像...
cd framework-user
docker build -t my-framework/user:1.0.0 .
cd ..

echo ======================================
echo Docker镜像构建完成！
echo ======================================

echo 查看构建的镜像：
docker images | findstr my-framework

echo ======================================
echo 构建完成！现在可以运行: docker-compose up -d
echo ======================================
pause

