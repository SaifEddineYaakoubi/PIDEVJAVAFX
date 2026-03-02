@echo off
REM ============================================================
REM Smart Farm - Lancement avec OpenCV
REM ============================================================

REM Configurations OpenCV possibles
set OPENCV_PATH_1=C:\opencv\build\bin\Release
set OPENCV_PATH_2=C:\opencv\build\bin
set OPENCV_PATH_3=C:\opencv\build\x64\vc15\bin

echo.
echo ============================================================
echo   Smart Farm - OpenCV Setup & Launch
echo ============================================================
echo.

REM Vérifier si OpenCV est installé
echo [Check] Searching for OpenCV DLL...
if exist "%OPENCV_PATH_1%\opencv_java490.dll" (
    echo [Found] OpenCV DLL at: %OPENCV_PATH_1%
    set OPENCV_PATH=%OPENCV_PATH_1%
    goto FOUND
) else if exist "%OPENCV_PATH_2%\opencv_java490.dll" (
    echo [Found] OpenCV DLL at: %OPENCV_PATH_2%
    set OPENCV_PATH=%OPENCV_PATH_2%
    goto FOUND
) else if exist "%OPENCV_PATH_3%\opencv_java490.dll" (
    echo [Found] OpenCV DLL at: %OPENCV_PATH_3%
    set OPENCV_PATH=%OPENCV_PATH_3%
    goto FOUND
) else (
    echo [Warning] OpenCV DLL not found in standard locations!
    echo.
    echo [Info] Checked paths:
    echo   - %OPENCV_PATH_1%
    echo   - %OPENCV_PATH_2%
    echo   - %OPENCV_PATH_3%
    echo.
    echo [Solution] OpenCV 4.9.0 can be downloaded from:
    echo   https://opencv.org/releases/
    echo.
    echo [Note] Application will start without OpenCV, but Face ID will be disabled
    echo.
    set OPENCV_PATH=
    goto LAUNCHING
)

:FOUND
echo [OK] OpenCV path: %OPENCV_PATH%
echo.

:LAUNCHING
echo [Info] Launching Smart Farm application...
echo.

if defined OPENCV_PATH (
    echo [Command] mvn clean javafx:run -Djava.library.path="%OPENCV_PATH%"
    echo.
    mvn clean javafx:run -Djava.library.path="%OPENCV_PATH%"
) else (
    echo [Command] mvn clean javafx:run (without OpenCV)
    echo.
    mvn clean javafx:run
)

echo.
echo ============================================================
if errorlevel 1 (
    echo [Error] Application exited with error
) else (
    echo [Success] Application exited successfully
)
echo ============================================================
echo.
pause

