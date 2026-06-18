@echo off
title Library Management System — Server
color 0B

echo.
echo  =============================================
echo   Library Management System — Starting Server
echo  =============================================
echo.

set OUT=out
set LIB=lib

:: Check compiled classes exist
if not exist "%OUT%\com\library\Main.class" (
    echo  [ERROR] Compiled classes not found. Please run compile.bat first!
    pause & exit /b 1
)

echo  [INFO] Starting server on http://localhost:8080 ...
echo  [INFO] Press Ctrl+C to stop.
echo.

java -cp "%OUT%;%LIB%\*" com.library.Main

pause
