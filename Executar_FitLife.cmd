@echo off
setlocal
chcp 65001 >nul
title FitLife - Marcos Paulo Santos Mota
cd /d "%~dp0"
set "JAVA_FITLIFE=%~dp0..\..\work\tools\jdk17\jdk-17.0.20.1+1\bin\java.exe"
if exist "%JAVA_FITLIFE%" goto localizar_jar
if defined JAVA_HOME (
    set "JAVA_FITLIFE=%JAVA_HOME%\bin\java.exe"
)
if exist "%JAVA_FITLIFE%" goto localizar_jar
where java >nul 2>nul
if errorlevel 1 (
    echo Configure um JDK 17 ou superior em JAVA_HOME ou PATH. Consulte o README.
    pause
    exit /b 1
)
set "JAVA_FITLIFE=java"
:localizar_jar
set "JAR_FITLIFE=distribuicao\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
set "JAR_FITLIFE=target\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
set "JAR_FITLIFE=build\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
echo JAR ainda nao gerado. Execute compilar-executar.cmd ou mvn clean verify.
pause
exit /b 1
:executar
"%JAVA_FITLIFE%" -Dfile.encoding=UTF-8 -jar "%JAR_FITLIFE%"
set "FITLIFE_RESULT=%ERRORLEVEL%"
echo(
pause
exit /b %FITLIFE_RESULT%
