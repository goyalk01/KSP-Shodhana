#!/usr/bin/env bash
set -e

echo "--------------------------------------------------------"
echo "Deploying KSP Shodhana Workspace via Docker..."
echo "--------------------------------------------------------"

if [ ! -f .env ]; then
  echo "No .env file found. Copying .env.example..."
  cp .env.example .env
fi

if command -v docker-compose &> /dev/null; then
  docker-compose up --build -d
else
  docker compose up --build -d
fi

echo ""
echo "--------------------------------------------------------"
echo "KSP Shodhana is live and running!"
echo "--------------------------------------------------------"
echo "Frontend:   http://localhost:3000"
echo "Backend:    http://localhost:8080"
echo "AI Service: http://localhost:8000"
echo "--------------------------------------------------------"
