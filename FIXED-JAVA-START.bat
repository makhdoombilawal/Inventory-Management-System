@echo off
echo ============================================================
echo   AUTO JAVA SETUP + SWAGGER UI (JAVA_HOME FIX)
echo ============================================================
echo.
echo [INFO] Ensuring no existing Java processes are blocking port 8080...
taskkill /F /IM java.exe 2>nul
echo Done.
echo.
cd /d "e:\InventoryApp\Inventory Management System"

REM Auto-detect Java installation
echo [INFO] Auto-detecting Java installation...

REM Try common Java locations
for %%i in ("C:\Program Files\Java\jdk-22" "C:\Program Files\Java\jdk-21" "C:\Program Files\Java\jdk-17" "C:\Program Files\OpenJDK\*" "C:\Program Files\Eclipse Adoptium\*" "C:\Program Files\Microsoft\*" "C:\Program Files (x86)\Java\jdk*") do (
    if exist "%%~i\bin\java.exe" (
        set "DETECTED_JAVA_HOME=%%~i"
        goto :java_found
    )
)

REM If not found, try java command directly
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Java found in PATH, trying to detect JAVA_HOME...
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%g
    echo [INFO] Java version: %JAVA_VERSION%
    
    REM Use java command directly without JAVA_HOME
    goto :compile_direct
)

echo [ERROR] ❌ Java not found!
echo.
echo PLEASE INSTALL JAVA 17+ from one of these:
echo   • https://adoptium.net/ (Recommended)
echo   • https://www.oracle.com/java/technologies/javase-downloads.html
echo   • https://docs.microsoft.com/en-us/java/openjdk/download
echo.
pause
exit /b 1

:java_found
echo [SUCCESS] ✅ Java found at: %DETECTED_JAVA_HOME%
set "JAVA_HOME=%DETECTED_JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [INFO] JAVA_HOME automatically set to: %JAVA_HOME%
goto :compile

:compile_direct
echo [INFO] Using java from PATH (no JAVA_HOME needed)
goto :compile

:compile
echo.
echo ============================================================
echo   PREPARING ^& COMPILING APPLICATION
echo ============================================================
echo.
echo [PREP] Removing duplicate VehicleRepository file...
del "src\main\java\com\inventoryapp\vehicle\repository\VehicleRepository_FIXED.java" 2>nul
echo [PREP] File cleanup complete!
echo.
echo [1/2] Running Maven compilation...
call .\mvnw.cmd clean compile -DskipTests && goto :compile_ok || goto :compile_fail

:compile_fail
echo [ERROR] ❌ Compilation failed with code %ERRORLEVEL%
echo.
echo TROUBLESHOOTING:
echo 1. Check Java version: java -version (needs 17+)
echo 2. Try: .\mvnw.cmd clean compile -DskipTests (verbose)
echo 3. Check project structure and pom.xml
echo.
pause
exit /b 1

:compile_ok
echo [SUCCESS] ✅ Compilation successful!
echo.

echo [2/2] Starting Spring Boot application...
echo.
echo 🚀 STARTING ENTERPRISE AUTH MODULE WITH SWAGGER UI
echo.
echo 📍 URLs (will open automatically):
echo   • http://localhost:8080/swagger-ui.html
echo   • http://localhost:8080/swagger-ui/index.html  
echo.
echo 🔐 LOGIN CREDENTIALS (EMAIL BASED):
echo   • Email: admin@system.com
echo   • Password: Admin@123
echo   • X-Tenant-Id: 0 (System Tenant)
echo.
echo 📋 QUICK TEST STEPS:
echo   1. Wait for "Started InventoryManagementApplication"
echo   2. Browser opens to Swagger UI automatically
echo   3. Test /api/auth/login with credentials above
echo   4. Copy JWT token, click "Authorize" button
echo   5. Test protected endpoints with JWT
echo.
echo ⚠️  Press Ctrl+C to stop application
echo ============================================================
echo.

REM Start application and open browser after delay
start /b cmd /c "call .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test"

echo Waiting for application to start (30 seconds)...
timeout /t 30 /nobreak >nul

echo Opening Swagger UI in your browser...
start http://localhost:8080/swagger-ui.html

echo.
echo 🎉 SWAGGER UI SHOULD NOW BE OPEN!
echo.
echo If browser didn't open, go to: http://localhost:8080/swagger-ui.html
echo.
echo Application is running... Press any key to stop.
pause >nul

echo.
echo Stopping application...
taskkill /F /IM java.exe 2>nul
echo Application stopped.
pause