# KtorDockerTest
To start the containers run the following commands in the ./KtorServer directory: 
./gradlew installDist
docker build -t ktor-server .
docker-compose up

To stop the containers run the following commands in the ./KtorServer directory: 
docker-compose stop

After modifying the server code, the image for the server needs to be removed and a build has to be triggered again: 
docker image rm ktorserver
