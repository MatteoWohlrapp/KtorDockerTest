#!/bin/zsh

curl -X GET  "http://0.0.0.0:8080/scores?userId=10"
printf "\n"

curl -X GET  "http://0.0.0.0:8080/users?ids=1&ids=2"
printf "\n"

curl -X GET  "http://0.0.0.0:8080/competitions?userId=1"
printf "\n"

curl -X GET  "http://0.0.0.0:8080/competitions/3"
printf "\n"

curl -X PUT -d "userId=12" "http://0.0.0.0:8080/scores"
printf "\n"
