@echo off
setlocal EnableDelayedExpansion
pushd "%~dp0"
title Smart Expense Tracker

set "JAVA_BIN=%~dp0.jdk\bin\java.exe"
set "APP_CLASSES=%~dp0app\BOOT-INF\classes"
set "APP_RES=%~dp0src\main\resources"
set "APP_LIB=%~dp0app\BOOT-INF\lib\*"
set "MAIN_CLASS=com.expense.ledger.LedgerApplication"
set "APP_URL=http://localhost:8080/login"
set "LOG=%~dp0ledger.log"

if not exist "%JAVA_BIN%" (
    echo [ERROR] Bundled Java runtime missing at .jdk\bin\java.exe
    pause & popd & exit /b 1
)
if not exist "%APP_CLASSES%" (
    echo [ERROR] app\BOOT-INF\classes not found.
    pause & popd & exit /b 1
)

call :KILL_PORT_8080

cls
echo.
echo   ===========================================================
echo               S M A R T   E X P E N S E   T R A C K E R
echo   ===========================================================
echo.
echo     [*] Runtime   : Bundled JDK (.jdk)
echo     [*] Sources   : src\main\java  (edit .java files here)
echo     [*] Resources : src\main\resources  (templates/static/props)
echo     [*] URL       : %APP_URL%
echo     [*] Log file  : ledger.log
echo.

rem --- Compile sources if a java source tree exists ---
if exist "%~dp0src\main\java" (
    echo     [BUILD] Compiling Java sources...
    call "%~dp0build.bat"
    if errorlevel 1 (
        echo     [BUILD] Compile failed. See output above.
        popd & exit /b 1
    )
    echo     [BUILD] OK.
)

echo.
echo   -----------------------------------------------------------
echo     Booting application - this takes a few seconds...
echo   -----------------------------------------------------------
echo.

rem --- Open the browser once the server is ready ---
start "" /B cmd /c "ping -n 12 127.0.0.1 >nul & start """" ""%APP_URL%"""

rem --- Run directly from folders (src resources + compiled classes) ---
"%JAVA_BIN%" -XX:TieredStopAtLevel=1 -Xshare:auto ^
  -Dspring.main.banner-mode=off ^
  -Dspring.output.ansi.enabled=never ^
  -Dlogging.pattern.console="  %%d{HH:mm:ss}  %%-5level  %%msg%%n" ^
  -Dlogging.level.root=WARN ^
  -Dlogging.level.org.springframework.boot.web.embedded.tomcat.TomcatWebServer=INFO ^
  -Dlogging.level.org.springframework.boot.web.servlet.context=INFO ^
  -Dlogging.level.com.expense.ledger=INFO ^
  -Dlogging.file.name="%LOG%" ^
  -cp "%APP_RES%;%APP_CLASSES%;%APP_LIB%" %MAIN_CLASS%

echo.
echo   -----------------------------------------------------------
echo     Application stopped. Releasing port 8080...
echo   -----------------------------------------------------------
call :KILL_PORT_8080
echo     Done. Press any key to close this window.
pause >nul
popd
endlocal
exit /b 0

:KILL_PORT_8080
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /r /c:":8080 .*LISTENING"') do (
    taskkill /F /PID %%P >nul 2>&1
)
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /r /c:":8080.*LISTENING"') do (
    taskkill /F /PID %%P >nul 2>&1
)
exit /b 0
