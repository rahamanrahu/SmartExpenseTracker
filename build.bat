@echo off
setlocal
pushd "%~dp0"

set "JAVAC=%~dp0.jdk\bin\javac.exe"
set "SRC_DIR=%~dp0src\main\java"
set "OUT_DIR=%~dp0app\BOOT-INF\classes"
set "LIB_DIR=%~dp0app\BOOT-INF\lib\*"

if not exist "%JAVAC%" (
    echo [BUILD] ERROR: javac not found at %JAVAC%
    popd & exit /b 1
)
if not exist "%SRC_DIR%" (
    echo [BUILD] ERROR: source directory missing: %SRC_DIR%
    popd & exit /b 1
)

dir /s /b "%SRC_DIR%\*.java" > "%TEMP%\ledger-sources.txt" 2>nul

"%JAVAC%" -d "%OUT_DIR%" -cp "%OUT_DIR%;%LIB_DIR%" -Xlint:none -nowarn @"%TEMP%\ledger-sources.txt" 2>&1
set "RC=%ERRORLEVEL%"
del "%TEMP%\ledger-sources.txt" >nul 2>&1

if not "%RC%"=="0" (
    echo.
    echo [BUILD] Compile failed (exit %RC%^).
    popd & exit /b %RC%
)

popd
endlocal
exit /b 0
