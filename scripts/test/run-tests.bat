@echo off
REM 测试环境运行测试脚本 (Windows)

echo ==========================================
echo 测试环境 - 运行所有测试
echo ==========================================
echo.

echo 📦 运行 Maven 测试...
call mvn clean test

if %errorlevel% neq 0 (
    echo ❌ 测试失败！
    pause
    exit /b 1
)

echo.
echo ==========================================
echo ✅ 所有测试完成！
echo ==========================================
echo 📊 查看测试报告: target\surefire-reports\
echo.

pause

