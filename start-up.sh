#!/bin/bash

# export the enviroment variables


export $(cat local.env | xargs)

# Build the application (if necessary)
./mvnw clean package -DskipTests  # For Maven

docker build -t spring-boot-main .

# Run the Spring Boot app
java -jar target/*.jar  # Adjust path if necessary

