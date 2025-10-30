@echo off
REM 生产环境健康检查脚本 (Windows)

setlocal enabledelayedexpansion

echo ==========================================
echo 生产环境健康检查
echo ==========================================
echo.

set FAIL_COUNT=0

REM 检查 Gateway
echo 检查 Gateway...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Gateway 正常
) else (
    echo ❌ Gateway 异常
    set /a FAIL_COUNT+=1
)

REM 检查 Auth Service
echo 检查 Auth Service...
curl -s -o nul -w "%%{http_code}" http://localhost:8081/actuator/health | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Auth Service 正常
) else (
    echo ❌ Auth Service 异常
    set /a FAIL_COUNT+=1
)

REM 检查 User Service
echo 检查 User Service...
curl -s -o nul -w "%%{http_code}" http://localhost:8082/actuator/health | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ User Service 正常
) else (
    echo ❌ User Service 异常
    set /a FAIL_COUNT+=1
)

REM 检查 Nacos
echo 检查 Nacos...
curl -s -o nul -w "%%{http_code}" http://localhost:8848/nacos | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Nacos 正常
) else (
    echo ❌ Nacos 异常
    set /a FAIL_COUNT+=1
)

REM 检查 SkyWalking UI
echo 检查 SkyWalking UI...
curl -s -o nul -w "%%{http_code}" http://localhost:8088 | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ SkyWalking UI 正常
) else (
    echo ❌ SkyWalking UI 异常
    set /a FAIL_COUNT+=1
)

echo.
if %FAIL_COUNT% equ 0 (
    echo ✅ 所有服务健康检查通过！
) else (
    echo ❌ 有 %FAIL_COUNT% 个服务健康检查失败！
)

exit /b %FAIL_COUNT%

