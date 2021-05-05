# KtorDockerTest
## Starting the container: 
  To start the containers run the following commands in the ./KtorServer directory: <br/>
    ./gradlew installDist <br/>
    docker build -t ktor-server . <br/>
    docker-compose up <br/>

## Stopping the container: 
  To stop the containers run the following commands in the ./KtorServer directory: <br/>
    docker-compose stop

  After modifying the server code, the image for the server needs to be removed and a build has to be triggered again: <br/>
    docker image rm ktorserver
