@echo off
chcp 65001 >nul
echo ======================================
echo 启动微服务项目
echo ======================================

echo 1. 创建数据目录...
if not exist "docker\mysql\data" mkdir "docker\mysql\data"
if not exist "docker\mysql\conf" mkdir "docker\mysql\conf"
if not exist "docker\mysql\init" mkdir "docker\mysql\init"
if not exist "docker\redis\data" mkdir "docker\redis\data"
if not exist "docker\redis\conf" mkdir "docker\redis\conf"
if not exist "docker\nacos\logs" mkdir "docker\nacos\logs"
if not exist "docker\prometheus\data" mkdir "docker\prometheus\data"
if not exist "docker\grafana\data" mkdir "docker\grafana\data"

echo 2. 启动基础设施服务（MySQL, Redis, Nacos, Sentinel）...
docker-compose up -d mysql redis nacos sentinel prometheus grafana

echo 3. 等待基础设施服务启动（约60秒）...
timeout /t 60 /nobreak

echo 4. 检查基础设施服务状态...
docker-compose ps

echo 5. 启动微服务应用...
docker-compose up -d gateway auth user

echo ======================================
echo 所有服务已启动！
echo ======================================
echo.
echo 服务访问地址：
echo   - 网关服务:       http://localhost:8080
echo   - 认证服务:       http://localhost:8081
echo   - 用户服务:       http://localhost:8082
echo   - Nacos控制台:   http://localhost:8848/nacos (nacos/nacos)
echo   - Sentinel控制台: http://localhost:8858 (sentinel/sentinel)
echo   - Prometheus:    http://localhost:9090
echo   - Grafana:       http://localhost:3000 (admin/admin)
echo   - MySQL:         localhost:3306 (root/root)
echo   - Redis:         localhost:6379
echo.
echo 查看日志命令：
echo   docker-compose logs -f [service-name]
echo.
echo 停止所有服务命令：
echo   docker-compose down
echo ======================================
pause

