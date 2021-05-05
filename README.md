# KtorDockerTest
## Starting the container: 
  To start the containers run the following command in the /KtorServer directory: <br/>
  
    ./start_container.zsh   
    
  Or you can run the following commands: 
  
    ./gradlew installDist 
    docker build -t ktor-server . 
    docker-compose up -d 
    

 ## Running the server: 
  After the containers are started, the server automatically starts. To test if it is working try: <br/>  
  
  curl -X GET  "http://0.0.0.0:8080/"

## Stopping the container: 

  To stop the containers and delete the image run the following command in the /KtorServer directory: <br/>
  
     ./stop_container.zsh
     
  Or you can run the following commands: 
  
    docker-compose stop
    docker container rm ktorserver_server_1
    docker image rm ktorserver
     
    
    

