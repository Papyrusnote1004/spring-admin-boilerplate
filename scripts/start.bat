@echo off
setlocal

:: ── 설정 ──────────────────────────────────────────────────────
set APP_DIR=%~dp0..
set JAR_NAME=spring-admin-boilerplate.jar
set PROFILE=local
set LOG_FILE=%APP_DIR%\app.log

:: DB 접속 정보 (필요 시 수정)
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=admindb
set DB_USER=admin
set DB_PASSWORD=admin
:: ──────────────────────────────────────────────────────────────

:: 이미 실행 중인지 확인
for /f "tokens=1" %%i in ('wmic process where "name='java.exe'" get ProcessId /format:value 2^>nul ^| findstr /r "[0-9]"') do (
    echo [WARNING] Java 프로세스가 이미 실행 중입니다. 먼저 stop.bat 을 실행하세요.
    pause
    exit /b 1
)

:: jar 빌드 (없으면 빌드)
if not exist "%APP_DIR%\build\libs\%JAR_NAME%" (
    echo [INFO] JAR 파일이 없어 빌드를 시작합니다...
    cd /d "%APP_DIR%"
    call gradlew.bat bootJar -x test
    if errorlevel 1 (
        echo [ERROR] 빌드 실패
        pause
        exit /b 1
    )
)

:: 백그라운드로 서버 시작
echo [INFO] 서버를 시작합니다...
start "SpringAdmin" /B java -jar "%APP_DIR%\build\libs\%JAR_NAME%" ^
    --spring.profiles.active=%PROFILE% ^
    --DB_HOST=%DB_HOST% ^
    --DB_PORT=%DB_PORT% ^
    --DB_NAME=%DB_NAME% ^
    --DB_USER=%DB_USER% ^
    --DB_PASSWORD=%DB_PASSWORD% ^
    > "%LOG_FILE%" 2>&1

echo [INFO] 서버가 시작됐습니다.
echo [INFO] 접속 주소: http://localhost:8080
echo [INFO] 로그 파일: %LOG_FILE%
echo.
echo 로그를 실시간으로 보려면: scripts\log.bat
