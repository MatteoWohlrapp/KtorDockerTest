# KtorDockerTest
## Starting the container: 
  To start the containers run the following commands in the ./KtorServer directory: <br/>
    ./gradlew installDist <br/>
    docker build -t ktor-server . <br/>
    docker-compose up <br/>

 ## Running the server: 
  After the containers are started, the server automatically starts. To test if it is working try: <br/>
  curl -X GET  "http://0.0.0.0:8080/"

## Stopping the container: 
  To stop the containers run the following commands in the ./KtorServer directory: <br/>
    docker-compose stop

  After modifying the server code, the image for the server needs to be removed and a build has to be triggered again: <br/>
    docker image rm ktorserver
    
    

