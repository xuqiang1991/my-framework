#!/bin/bash

echo "======================================"
echo "开始构建微服务项目"
echo "======================================"

# 清理并编译项目
echo "1. 清理并编译Maven项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Maven构建失败！"
    exit 1
fi

echo "======================================"
echo "Maven构建完成！"
echo "======================================"

# 构建Docker镜像
echo "2. 构建Docker镜像..."

echo "构建网关服务镜像..."
cd framework-gateway
docker build -t my-framework/gateway:1.0.0 .
cd ..

echo "构建认证服务镜像..."
cd framework-auth
docker build -t my-framework/auth:1.0.0 .
cd ..

echo "构建用户服务镜像..."
cd framework-user
docker build -t my-framework/user:1.0.0 .
cd ..

echo "======================================"
echo "Docker镜像构建完成！"
echo "======================================"

# 显示镜像列表
echo "查看构建的镜像："
docker images | grep my-framework

echo "======================================"
echo "构建完成！现在可以运行: docker-compose up -d"
echo "======================================"

