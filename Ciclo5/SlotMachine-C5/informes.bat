@echo off
rem Genera los informes de JaCoCo (cubrimiento) y PMD (reglas de calidad).
rem Uso:  informes.bat inicial    o    informes.bat final
rem   inicial: codigo del ciclo 4, antes de los cambios (carpeta codigo-inicial)
rem   final:   codigo actual (carpeta src)
if /i not "%~1"=="inicial" if /i not "%~1"=="final" (
    echo Uso: informes.bat inicial   o   informes.bat final
    exit /b 1
)

rem Se ejecuta siempre desde la carpeta del proyecto, aunque se llame desde otra
cd /d "%~dp0"
set SALIDA=..\informes\%~1
set CODIGO=src
if /i "%~1"=="inicial" set CODIGO=codigo-inicial\src
set JUNIT=lib\junit-platform-console-standalone-1.10.2.jar
set JACOCO=herramientas\jacoco\lib
set PMD=herramientas\pmd-bin-7.13.0\bin\pmd.bat

if not exist %JACOCO%\jacocoagent.jar (echo Falta JaCoCo en %JACOCO%. Ver README.TXT & exit /b 1)
if not exist %PMD% (echo Falta PMD en herramientas\pmd-bin-7.13.0. Ver README.TXT & exit /b 1)

echo ===== Informe %~1: codigo de %CODIGO% =====

rem 1. Compilar: el simulador en build\main y las pruebas en build\test.
rem    Asi JaCoCo mide solo las clases del simulador, no las de prueba.
if exist build rmdir /s /q build
mkdir build\main build\test
javac -encoding UTF-8 --release 21 -d build\main %CODIGO%\main\java\*.java || exit /b 1
javac -encoding UTF-8 --release 21 -d build\test -cp build\main;%JUNIT% %CODIGO%\test\java\*.java || exit /b 1

rem 2. Correr las pruebas de unidad con el agente de JaCoCo.
rem    La prueba de aceptacion se excluye porque necesita que un usuario responda.
if not exist %SALIDA% mkdir %SALIDA%
java -javaagent:%JACOCO%\jacocoagent.jar=destfile=build\jacoco.exec -jar %JUNIT% execute --class-path build\main;build\test --scan-class-path build\test --exclude-classname ".*AceptacionTest" --details=summary --disable-banner

rem 3. Informe de cubrimiento (HTML y CSV).
java -jar %JACOCO%\jacococli.jar report build\jacoco.exec --classfiles build\main --sourcefiles %CODIGO%\main\java --encoding UTF-8 --name SlotMachine --html %SALIDA%\jacoco --csv %SALIDA%\jacoco.csv

rem 4. PMD con las reglas quickstart de Java: informe HTML y XML.
call %PMD% check -d %CODIGO%\main\java -R rulesets/java/quickstart.xml --aux-classpath build\main -f html -r %SALIDA%\pmd.html --no-cache --no-progress
call %PMD% check -d %CODIGO%\main\java -R rulesets/java/quickstart.xml --aux-classpath build\main -f xml -r %SALIDA%\pmd.xml --no-cache --no-progress

rem 5. Este muestra cuantos avisos hay de cada prioridad (1 = rojo)
call %PMD% check -d %CODIGO%\main\java -R rulesets/java/quickstart.xml --aux-classpath build\main -f xslt -r %SALIDA%\pmd_prioridades.html --no-cache --no-progress

rem 6. Resumen de resultados en la terminal
powershell -NoProfile -ExecutionPolicy Bypass -File herramientas\resumen.ps1 %SALIDA% %~1

echo Informes en %SALIDA%: jacoco\index.html, pmd.html y pmd_prioridades.html
