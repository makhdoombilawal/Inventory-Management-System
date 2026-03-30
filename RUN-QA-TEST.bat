@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo   ENTERPRISE QA TEST RUNNER (JAVA_HOME FIX)
echo ============================================================

:: Detect Java
set "JAVA_PATH="
if exist "C:\Program Files\Java\jdk-22" set "JAVA_PATH=C:\Program Files\Java\jdk-22"
if not defined JAVA_PATH (
    for /d %%i in ("C:\Program Files\Java\jdk-*") do set "JAVA_PATH=%%i"
)

if not defined JAVA_PATH (
    echo [ERROR] No JDK found in C:\Program Files\Java\
    exit /b 1
)

set "JAVA_HOME=%JAVA_PATH%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [SUCCESS] Java found at: %JAVA_HOME%

echo.
echo [1/1] Running EnterpriseQATest...
call .\mvnw.cmd test -Dtest=EnterpriseQATest
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [FAILURE] Tests failed or build error occurred.
    exit /b 1
)

echo.
echo [SUCCESS] QA Tests completed successfully!
exit /b 0
