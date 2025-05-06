#!/bin/bash

cleanup() {
  echo "Closing backend (PID: $BACKEND_PID)..."
  kill $BACKEND_PID
  echo "All services closed"
  exit
}
trap cleanup SIGINT SIGTERM


echo "Starting backend"
cd backend
./gradlew bootRun &> backend.log &
BACKEND_PID=$!

until curl -s http://localhost:8080/actuator/health | grep -q 'UP'; do
  sleep 2
  echo "Waiting..."
done

printf "\nWarming up data\n"
until curl -s http://localhost:8080/comments | grep -q "\["; do
  sleep 2
  echo "Waiting..."
done


cd ../frontend
printf "\nStarting frontend\n"
npm install &> /dev/null
npm run dev &> /dev/null &

printf "\nBackend logs:\n\n"
tail -n 100 -f ../backend/backend.log
