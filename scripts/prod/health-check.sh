#!/bin/bash
# 生产环境健康检查脚本

echo "=========================================="
echo "生产环境健康检查"
echo "=========================================="

FAIL_COUNT=0

# 检查服务状态
check_service() {
    local service_name=$1
    local url=$2
    local expected_code=$3
    
    echo -n "检查 $service_name... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" --max-time 10)
    
    if [ "$response" == "$expected_code" ]; then
        echo "✅ 正常 (HTTP $response)"
    else
        echo "❌ 异常 (HTTP $response, 期望 $expected_code)"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
}

# 检查各个服务
check_service "Gateway" "http://localhost:8080/actuator/health" "200"
check_service "Auth Service" "http://localhost:8081/actuator/health" "200"
check_service "User Service" "http://localhost:8082/actuator/health" "200"
check_service "Nacos" "http://localhost:8848/nacos" "200"
check_service "SkyWalking UI" "http://localhost:8088" "200"

echo ""
if [ $FAIL_COUNT -eq 0 ]; then
    echo "✅ 所有服务健康检查通过！"
    exit 0
else
    echo "❌ 有 $FAIL_COUNT 个服务健康检查失败！"
    exit 1
fi

