#!/bin/bash

# export the enviroment variables


export $(cat local.env | xargs)

# Build the application (if necessary)
#./mvnw clean package -DskipTests  # For Maven

mvn clean package -DskipTests

sudo docker build -t lexilearn-spring-boot-main .

sudo docker compose up
# Run the Spring Boot app
# java -jar target/*.jar  # Adjust path if necessary

