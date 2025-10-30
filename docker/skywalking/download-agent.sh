#!/bin/bash
# SkyWalking Agent 下载脚本
# 用于下载并配置 SkyWalking Java Agent

set -e

SKYWALKING_VERSION="9.7.0"
AGENT_DIR="./docker/skywalking/agent"
DOWNLOAD_URL="https://archive.apache.org/dist/skywalking/${SKYWALKING_VERSION}/apache-skywalking-java-agent-${SKYWALKING_VERSION}.tgz"

echo "=========================================="
echo "SkyWalking Agent 下载脚本"
echo "版本: ${SKYWALKING_VERSION}"
echo "=========================================="

# 检查 agent 目录是否已存在
if [ -d "$AGENT_DIR" ]; then
    echo "⚠️  Agent 目录已存在: $AGENT_DIR"
    read -p "是否重新下载？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "跳过下载"
        exit 0
    fi
    rm -rf "$AGENT_DIR"
fi

# 创建临时目录
TMP_DIR=$(mktemp -d)
echo "📁 创建临时目录: $TMP_DIR"

# 下载 SkyWalking Agent
echo "📥 下载 SkyWalking Agent..."
cd "$TMP_DIR"

# 尝试从官方镜像下载
if ! wget -q --show-progress "$DOWNLOAD_URL"; then
    echo "❌ 从官方下载失败，尝试从备用地址下载..."
    # 备用下载地址（可以使用国内镜像）
    DOWNLOAD_URL="https://mirrors.tuna.tsinghua.edu.cn/apache/skywalking/${SKYWALKING_VERSION}/apache-skywalking-java-agent-${SKYWALKING_VERSION}.tgz"
    if ! wget -q --show-progress "$DOWNLOAD_URL"; then
        echo "❌ 下载失败，请检查网络连接或手动下载"
        rm -rf "$TMP_DIR"
        exit 1
    fi
fi

# 解压文件
echo "📦 解压文件..."
tar -zxf "apache-skywalking-java-agent-${SKYWALKING_VERSION}.tgz"

# 移动到目标目录
echo "📂 安装到目标目录..."
cd -
mkdir -p "$(dirname "$AGENT_DIR")"
mv "$TMP_DIR/skywalking-agent" "$AGENT_DIR"

# 清理临时文件
echo "🧹 清理临时文件..."
rm -rf "$TMP_DIR"

# 设置权限
chmod -R 755 "$AGENT_DIR"

echo ""
echo "✅ SkyWalking Agent 安装成功！"
echo "📍 安装路径: $AGENT_DIR"
echo "📝 配置文件: $AGENT_DIR/config/agent.config"
echo ""
echo "下一步："
echo "1. 查看配置文件并根据需要调整"
echo "2. 运行 docker-compose up 启动服务"
echo ""

