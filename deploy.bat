@echo off
echo --------------------------------------------------------
echo Deploying KSP Shodhana Workspace via Docker...
echo --------------------------------------------------------

if not exist .env (
    echo No .env file found. Copying .env.example...
    copy .env.example .env
)

where docker-compose >nul 2>nul
if %errorlevel%==0 (
    docker-compose up --build -d
) else (
    docker compose up --build -d
)

echo.
echo --------------------------------------------------------
echo KSP Shodhana is live and running!
echo --------------------------------------------------------
echo Frontend:   http://localhost:3000
echo Backend:    http://localhost:8080
echo AI Service: http://localhost:8000
echo --------------------------------------------------------
