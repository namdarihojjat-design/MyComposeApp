@if "%DEBUG%" == "" @echo off
setlocal
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
call "%DIRNAME%\gradle\wrapper\gradle-wrapper.jar" %*
