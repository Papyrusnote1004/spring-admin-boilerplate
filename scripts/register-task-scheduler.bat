@echo off
:: Windows 작업 스케줄러에 등록 — 부팅 시 자동 시작
:: 관리자 권한으로 실행 필요

net session >nul 2>&1
if errorlevel 1 (
    echo [ERROR] 관리자 권한으로 실행해주세요.
    echo 이 파일을 우클릭 후 "관리자 권한으로 실행" 선택
    pause
    exit /b 1
)

set TASK_NAME=SpringAdminBoilerplate
set START_BAT=%~dp0start.bat

:: 기존 작업 삭제 후 재등록
schtasks /delete /tn "%TASK_NAME%" /f >nul 2>&1

schtasks /create ^
    /tn "%TASK_NAME%" ^
    /tr "\"%START_BAT%\"" ^
    /sc ONSTART ^
    /delay 0000:30 ^
    /ru SYSTEM ^
    /rl HIGHEST ^
    /f

if errorlevel 1 (
    echo [ERROR] 작업 스케줄러 등록 실패
    pause
    exit /b 1
)

echo [SUCCESS] 작업 스케줄러 등록 완료!
echo [INFO] PC 부팅 시 30초 후 서버가 자동으로 시작됩니다.
echo.
echo 등록된 작업 확인:
schtasks /query /tn "%TASK_NAME%" /fo LIST
pause
