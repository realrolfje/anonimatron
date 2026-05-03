@echo off
setlocal

if "%JAVA_OPTS%"=="" set "JAVA_OPTS=-Xmx2G"
set "ADD_OPENS=--add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED"

for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION=%%~v"
if "%JAVA_VERSION:~0,3%"=="1.8" set "ADD_OPENS="

java %JAVA_OPTS% %ADD_OPENS% -classpath *;./libraries/*;./jdbcdrivers/*;./anonymizers/* com.rolfje.anonimatron.Anonimatron %*
