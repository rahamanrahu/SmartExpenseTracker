@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------
@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%
@IF "%MAVEN_BATCH_PAUSE%" == "on" pause

@setlocal

@SET MAVEN_PROJECT_BASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECT_BASEDIR%"=="" goto endDetectBaseDir

@SET EXEC_DIR=%CD%
@SET WDIR=%EXEC_DIR%
:findBaseDir
@IF EXIST "%WDIR%"\.mvn goto baseDirFound
@cd ..
@SET WDIR=%CD%
@IF NOT "%WDIR%"=="%EXEC_DIR%" goto findBaseDir
@SET MAVEN_PROJECT_BASEDIR=%EXEC_DIR%
@goto endDetectBaseDir
:baseDirFound
@SET MAVEN_PROJECT_BASEDIR=%WDIR%
:endDetectBaseDir
@cd "%EXEC_DIR%"

@SET MAVEN_HOME=%MAVEN_PROJECT_BASEDIR%\.mvn\wrapper\dist\apache-maven
@IF NOT EXIST "%MAVEN_HOME%" goto downloadMaven

:runMaven
@SET M2_HOME=%MAVEN_HOME%
@SET MAVEN_OPTS=%MAVEN_OPTS% -Xmx512m
"%MAVEN_HOME%\bin\mvn.cmd" %*
@goto end

:downloadMaven
@SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
@echo Downloading Maven 3.9.9...
@SET MAVEN_ZIP=%TEMP%\apache-maven-3.9.9-bin.zip
@SET MAVEN_EXTRACT=%MAVEN_PROJECT_BASEDIR%\.mvn\wrapper\dist

@powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%MAVEN_ZIP%'"
@if %ERRORLEVEL% neq 0 goto downloadFailed

@mkdir "%MAVEN_EXTRACT%" 2>nul
@powershell -Command "Expand-Archive -Force -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_EXTRACT%'"
@if %ERRORLEVEL% neq 0 goto downloadFailed
@rename "%MAVEN_EXTRACT%\apache-maven-3.9.9" apache-maven
@goto runMaven

:downloadFailed
@echo [ERROR] Failed to download Maven wrapper. Please install Maven manually.
@exit /b 1

:end
@endlocal
