@echo off
:: 로그 실시간 확인 (PowerShell tail -f 방식)
set LOG_FILE=%~dp0..\app.log

if not exist "%LOG_FILE%" (
    echo [WARNING] 로그 파일이 없습니다. 서버가 실행 중인지 확인하세요.
    pause
    exit /b 1
)

echo [INFO] 로그 실시간 출력 중... (종료: Ctrl+C)
powershell -command "Get-Content '%LOG_FILE%' -Wait -Tail 50"
