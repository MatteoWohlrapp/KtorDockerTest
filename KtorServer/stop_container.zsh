#!/bin/zsh

docker-compose stop
docker container rm ktorserver_server_1
docker image rm ktor-server