@echo off
REM =====================================================
REM Script para crear/recrear la base de datos EPaCe
REM =====================================================

echo =====================================================
echo  CREACION DE BASE DE DATOS E-Pa-Ce
echo =====================================================
echo.

REM Configuración de PostgreSQL (modifica según tu instalación)
set PGHOST=localhost
set PGPORT=5432
set PGUSER=postgres
set PGPASSWORD=PS8289
set PGDATABASE=postgres

echo Configuracion:
echo - Host: %PGHOST%
echo - Puerto: %PGPORT%
echo - Usuario: %PGUSER%
echo.

echo ADVERTENCIA: Este script eliminara y recreara la base de datos EPaCe
echo Todos los datos existentes se perdiran.
echo.
set /p CONFIRM="Deseas continuar? (S/N): "

if /i not "%CONFIRM%"=="S" (
    echo Operacion cancelada.
    pause
    exit /b 0
)

echo.
echo Ejecutando script de base de datos...
echo.

REM Ejecutar el script SQL
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %PGDATABASE% -f "src\main\resources\database-complete.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo =====================================================
    echo  BASE DE DATOS CREADA EXITOSAMENTE
    echo =====================================================
    echo.
    echo Usuarios de prueba creados:
    echo - admin@epace.com     / admin123  [ADMIN]
    echo - operador@epace.com  / test123   [OPERADOR]
    echo - monitor@epace.com   / test123   [MONITOR]
    echo - invitado@epace.com  / test123   [INVITADO]
    echo.
) else (
    echo.
    echo =====================================================
    echo  ERROR AL CREAR LA BASE DE DATOS
    echo =====================================================
    echo Verifica la configuracion de PostgreSQL
    echo.
)

pause
