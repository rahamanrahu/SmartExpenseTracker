@echo off
setlocal
pushd "%~dp0"

set "JAVA_HOME=%~dp0.jdk"
set "MVN=%~dp0.mvn\wrapper\dist\apache-maven\bin\mvn.cmd"

echo [mvn] Using JAVA_HOME: %JAVA_HOME%
echo [mvn] Running: mvn %*

"%MVN%" %*

set "RC=%ERRORLEVEL%"
popd
endlocal
exit /b %RC%
