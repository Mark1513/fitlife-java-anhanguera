@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    echo JDK 17 ou superior nao encontrado. Configure JAVA_HOME e PATH conforme o README.
    exit /b 1
)
if not exist build mkdir build
if not exist build\classes mkdir build\classes
javac --release 17 -encoding UTF-8 -d build/classes -sourcepath src/main/java src/main/java/fitlife/Main.java
if errorlevel 1 exit /b 1
jar -J-Djava.io.tmpdir=build --create --file build/fitlife.jar --main-class fitlife.Main -C build/classes .
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -jar build\fitlife.jar
endlocal
