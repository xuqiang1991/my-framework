#!/bin/bash
# 生产环境部署脚本

set -e

echo "=========================================="
echo "生产环境部署脚本"
echo "=========================================="

# 检查环境
if [ ! -f ".env.prod" ]; then
    echo "❌ 错误：未找到 .env.prod 配置文件"
    echo "请先创建 .env.prod 文件并配置生产环境参数"
    exit 1
fi

# 加载生产环境配置
source .env.prod

echo "📋 环境检查..."
# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装"
    exit 1
fi

# 检查 Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi

echo "✅ 环境检查通过"

# 停止旧服务
echo "🛑 停止旧服务..."
docker-compose -f docker-compose.prod.yml down

# 备份数据
echo "💾 备份数据..."
BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
if [ -d "docker/mysql/data" ]; then
    cp -r docker/mysql/data "$BACKUP_DIR/mysql"
    echo "✅ MySQL 数据已备份到 $BACKUP_DIR/mysql"
fi

# 构建镜像
echo "🔨 构建生产环境镜像..."
docker-compose -f docker-compose.prod.yml build

# 启动服务
echo "🚀 启动生产环境服务..."
docker-compose -f docker-compose.prod.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 健康检查
echo "🏥 执行健康检查..."
./scripts/prod/health-check.sh

echo ""
echo "=========================================="
echo "✅ 生产环境部署完成！"
echo "=========================================="
echo "📊 查看服务状态: docker-compose -f docker-compose.prod.yml ps"
echo "📝 查看日志: docker-compose -f docker-compose.prod.yml logs -f [service]"
echo ""

