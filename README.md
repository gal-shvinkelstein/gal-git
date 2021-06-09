# gal-git

# The lobby projects
# Project brief:
Main server that provide client registration and login services and allow to create a lobby for friends that provide multiplie choises of online games for group of friends to play together.
lobby server provide the logic to manage the chosen game in runtime while the client implemantation provide the logic to play the same game

# Project modules:
2 server implemantation:
1. MyGameServer - traditional java
2. server_spring(in process) - implemants same logic using maven spring boot, adding mysql DB to backup clients data 
(TODO: add unit tests for server_spring)

client implemantation:
Gamer - currently used also for server testing

# Project run instractions:
Currently implemanted only the Backend part of the project. 
In order to run you should choose one of the two servers provided**
1. For the first option run using your JVM
2. For the second option run as Maven Spring boot app (make sure you have Mysql installed on your machine) 

** Currently only option one is in production mode

In perallel run few clients on your JVM and follow the command line instruction to register, login create a lobby and play
