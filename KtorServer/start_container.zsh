#!/bin/zsh

./gradlew installDist
docker build -t ktor-server .
docker-compose up