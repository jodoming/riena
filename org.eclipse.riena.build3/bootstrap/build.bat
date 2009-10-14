set TOOLSROOT=c:\build3\tools
set JAVA_HOME=%TOOLSROOT%\jdk1.5.0_18
set ANT_HOME=%TOOLSROOT%\apache-ant-1.7.1
set CVS_HOME_BIN=%TOOLSROOT%\cygwin\bin
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%CVS_HOME_BIN%

c:
cd \build3
@echo off

REM ### CHECKS
cvs -version
echo.
java -version
echo.

if '%1' EQU 'build' GOTO :BUILD
if '%1' EQU 'runtests' GOTO :RUNTESTS

echo Usage:
echo build build	- Build Riena
echo build runtests	- Run tests (must build first)
GOTO :EOF

:BUILD
ant -f build.xml clean build
GOTO :EOF

:RUNTESTS
ant -f build.xml clean runtests
GOTO :EOF
