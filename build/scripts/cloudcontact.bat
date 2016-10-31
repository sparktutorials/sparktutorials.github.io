@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  cloudcontact startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and CLOUDCONTACT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\cloudcontact-1.0.0.jar;%APP_HOME%\lib\gson-2.5.jar;%APP_HOME%\lib\jsoup-1.9.2.jar;%APP_HOME%\lib\spark-core-2.5.2-SNAPSHOT.jar;%APP_HOME%\lib\slf4j-api-1.7.21.jar;%APP_HOME%\lib\slf4j-simple-1.7.21.jar;%APP_HOME%\lib\morphia-1.2.1.jar;%APP_HOME%\lib\handlebars-guava-cache-4.0.1.jar;%APP_HOME%\lib\handlebars-4.0.1.jar;%APP_HOME%\lib\jackson-core-2.8.4.jar;%APP_HOME%\lib\jackson-databind-2.8.4.jar;%APP_HOME%\lib\jackson-annotations-2.8.4.jar;%APP_HOME%\lib\spark-template-handlebars-2.3.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\srp6a-1.5.3.jar;%APP_HOME%\lib\mongo-java-driver-3.2.2.jar;%APP_HOME%\lib\cglib-nodep-2.2.2.jar;%APP_HOME%\lib\proxytoys-1.0.jar;%APP_HOME%\lib\guava-14.0.1.jar;%APP_HOME%\lib\commons-lang3-3.1.jar;%APP_HOME%\lib\antlr4-runtime-4.5.1-1.jar;%APP_HOME%\lib\rhino-1.7R4.jar;%APP_HOME%\lib\spark-core-2.3.jar;%APP_HOME%\lib\jetty-server-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-webapp-9.3.2.v20150730.jar;%APP_HOME%\lib\websocket-server-9.3.2.v20150730.jar;%APP_HOME%\lib\websocket-servlet-9.3.2.v20150730.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\jetty-http-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-io-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-xml-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-servlet-9.3.2.v20150730.jar;%APP_HOME%\lib\websocket-common-9.3.2.v20150730.jar;%APP_HOME%\lib\websocket-client-9.3.2.v20150730.jar;%APP_HOME%\lib\websocket-api-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-util-9.3.2.v20150730.jar;%APP_HOME%\lib\jetty-security-9.3.2.v20150730.jar

@rem Execute cloudcontact
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %CLOUDCONTACT_OPTS%  -classpath "%CLASSPATH%" App %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable CLOUDCONTACT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%CLOUDCONTACT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
