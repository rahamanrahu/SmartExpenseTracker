@echo off
setlocal enabledelayedexpansion

:: ============================================================
:: Smart Expense Tracker — Git Cleanup + Commit + Push
:: ============================================================
:: This script:
::   1. Checks git is available
::   2. Removes tracked files that should now be ignored
::   3. Stages .gitignore and all source changes
::   4. Commits and pushes to origin/main
:: ============================================================

set "REPO_DIR=%~dp0"
cd /d "%REPO_DIR%"

echo.
echo ============================================================
echo  Smart Expense Tracker — Git Cleanup + Commit + Push
echo ============================================================
echo.

:: ── 1. Verify git is available ────────────────────────────
where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] git not found in PATH.
    echo         Please install Git from https://git-scm.com/download/win
    echo         and re-run this script.
    pause
    exit /b 1
)

git --version
echo.

:: ── 2. Confirm we are inside a git repo ───────────────────
git rev-parse --git-dir >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] This directory is not a git repository.
    echo         Run: git init  (then set remote and try again)
    pause
    exit /b 1
)

:: ── 3. Remove now-ignored files from git index only ───────
echo [STEP 1] Removing tracked files that are now gitignored ...
echo          (files are NOT deleted from disk)
echo.

git rm -r --cached target/          2>nul
git rm -r --cached .jdk/            2>nul
git rm -r --cached app/             2>nul
git rm -r --cached SmartExpenseTracker/ 2>nul
git rm -r --cached .vscode/         2>nul
git rm    --cached export.txt       2>nul
git rm    --cached export.py        2>nul
git rm    --cached vineflower*.jar  2>nul
git rm    --cached *.log            2>nul
git rm    --cached *.gz             2>nul

echo.
echo [STEP 1] Done.
echo.

:: ── 4. Stage everything remaining ─────────────────────────
echo [STEP 2] Staging .gitignore and source files ...
git add .gitignore
git add pom.xml
git add src/
git add .mvn/
git add mvnw.cmd mvn.bat build.bat run-app.bat setup-maven.ps1 2>nul
git add .github/ 2>nul
echo.

:: ── 5. Commit ─────────────────────────────────────────────
echo [STEP 3] Committing ...
git commit -m "chore: add .gitignore, remove build artifacts and junk files

- Add comprehensive .gitignore for Spring Boot + Maven project
- Untrack: target/, .jdk/, app/, SmartExpenseTracker/ (nested dup)
- Untrack: *.log, *.gz, export.txt, vineflower jar, .vscode/
- Keep: src/, pom.xml, .mvn/, wrapper scripts, .github/"

if %ERRORLEVEL% NEQ 0 (
    echo [WARN] Nothing new to commit, or commit failed.
)
echo.

:: ── 6. Push ───────────────────────────────────────────────
echo [STEP 4] Pushing to origin ...
git push origin main
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] Push failed. You may need to:
    echo        - Check your remote: git remote -v
    echo        - Authenticate: git config credential.helper manager
    echo        - Try: git push origin HEAD
)
echo.

echo ============================================================
echo  Done! Check https://github.com/rahamanrahu/SmartExpenseTracker
echo ============================================================
pause
