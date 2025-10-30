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
    echo "请安装 JDK 21"
fi

# 检查Maven
echo ""
echo "检查 Maven..."
if command -v mvn &> /dev/null; then
    mvn -version 2>&1 | head -n 1
    echo "✓ Maven 已安装"
else
    echo "✗ Maven 未安装"
    echo "请运行: sudo apt install maven (Ubuntu/Debian)"
    echo "       brew install maven (macOS)"
fi

# 检查Docker
echo ""
echo "检查 Docker..."
if command -v docker &> /dev/null; then
    docker --version
    echo "✓ Docker 已安装"
else
    echo "✗ Docker 未安装"
    echo "请运行: curl -fsSL https://get.docker.com | sh"
fi

# 检查Docker Compose
echo ""
echo "检查 Docker Compose..."
if command -v docker-compose &> /dev/null; then
    docker-compose --version
    echo "✓ Docker Compose 已安装"
else
    echo "✗ Docker Compose 未安装"
    echo "请运行: sudo apt install docker-compose (Ubuntu/Debian)"
    echo "       brew install docker-compose (macOS)"
fi

echo ""
echo "========================================"
echo "检查完成"
echo "========================================"
echo ""
echo "如果所有组件都已安装，可以运行:"
echo "  ./build.sh   - 编译项目"
echo "  ./start.sh   - 启动服务"
echo ""
echo "如果缺少组件，请查看 SETUP.md 获取详细安装说明"
echo "========================================"


