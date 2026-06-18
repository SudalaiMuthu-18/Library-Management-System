@echo off
title Library Management System — Compiler
color 0A

echo.
echo  ============================================
echo   Library Management System — Build Script
echo  ============================================
echo.

set SRC=src
set OUT=out
set LIB=lib

:: Create output directory
if not exist "%OUT%" mkdir "%OUT%"

:: Collect all .java source files
dir /s /b "%SRC%\*.java" > sources.txt 2>nul

if not exist sources.txt (
    echo  [ERROR] No Java source files found in %SRC%
    pause & exit /b 1
)

echo  [INFO] Compiling Java sources with library dependencies...
echo.

javac -cp "%LIB%\*" -d "%OUT%" -sourcepath "%SRC%" @sources.txt

del sources.txt

if %ERRORLEVEL% == 0 (
    echo.
    echo  [SUCCESS] Compilation complete! Classes are in the out\ folder.
    echo  Run  run.bat  to start the server.
) else (
    echo.
    echo  [FAILED] Compilation failed. Check errors above.
)

echo.
pause
