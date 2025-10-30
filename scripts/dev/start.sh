#!/bin/bash

echo "======================================"
echo "启动微服务项目"
echo "======================================"

# 创建必要的目录
echo "1. 创建数据目录..."
mkdir -p docker/mysql/data
mkdir -p docker/mysql/conf
mkdir -p docker/mysql/init
mkdir -p docker/redis/data
mkdir -p docker/redis/conf
mkdir -p docker/nacos/logs
mkdir -p docker/prometheus/data
mkdir -p docker/grafana/data

# 设置目录权限
chmod -R 777 docker/mysql/data
chmod -R 777 docker/redis/data
chmod -R 777 docker/nacos/logs
chmod -R 777 docker/prometheus/data
chmod -R 777 docker/grafana/data

# 启动基础设施服务
echo "2. 启动基础设施服务（MySQL, Redis, Nacos, Sentinel）..."
docker-compose up -d mysql redis nacos sentinel prometheus grafana

# 等待基础设施服务就绪
echo "3. 等待基础设施服务启动（约60秒）..."
sleep 60

# 检查服务状态
echo "4. 检查基础设施服务状态..."
docker-compose ps

# 启动微服务
echo "5. 启动微服务应用..."
docker-compose up -d gateway auth user

echo "======================================"
echo "所有服务已启动！"
echo "======================================"
echo ""
echo "服务访问地址："
echo "  - 网关服务:       http://localhost:8080"
echo "  - 认证服务:       http://localhost:8081"
echo "  - 用户服务:       http://localhost:8082"
echo "  - Nacos控制台:   http://localhost:8848/nacos (nacos/nacos)"
echo "  - Sentinel控制台: http://localhost:8858 (sentinel/sentinel)"
echo "  - Prometheus:    http://localhost:9090"
echo "  - Grafana:       http://localhost:3000 (admin/admin)"
echo "  - MySQL:         localhost:3306 (root/root)"
echo "  - Redis:         localhost:6379"
echo ""
echo "查看日志命令："
echo "  docker-compose logs -f [service-name]"
echo ""
echo "停止所有服务命令："
echo "  docker-compose down"
echo "======================================"

