@echo off
echo [INFO] Spring Boot 서버를 종료합니다...

:: 8080 포트를 사용하는 프로세스 PID 찾아서 종료
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [INFO] PID %%a 종료 중...
    taskkill /PID %%a /F >nul 2>&1
    echo [INFO] 서버가 종료됐습니다.
    goto :done
)

echo [WARNING] 실행 중인 서버를 찾지 못했습니다.
:done
pause
