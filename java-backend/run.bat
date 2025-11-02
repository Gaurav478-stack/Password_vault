@echo off
REM Batch script to compile and run SecurePass Java Backend

echo ========================================
echo   SecurePass Java Backend - Build
echo ========================================
echo.

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

echo Compiling Java files...
javac -d bin src\main\java\com\securepass\model\*.java src\main\java\com\securepass\service\*.java src\main\java\com\securepass\service\impl\*.java src\main\java\com\securepass\manager\*.java src\main\java\com\securepass\api\*.java src\main\java\com\securepass\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   Compilation Successful!
    echo ========================================
    echo.
    echo Running SecurePass Application...
    echo.
    java -cp bin com.securepass.SecurePassApplication
) else (
    echo.
    echo ========================================
    echo   Compilation Failed!
    echo ========================================
    echo Please check the error messages above.
)

echo.
pause
