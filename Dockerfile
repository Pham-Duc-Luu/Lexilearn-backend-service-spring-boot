# Use an official OpenJDK image as the base
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/*.jar app.jar

# Copy your environment file (optional, if you use it inside container)
# COPY local.env .

# Expose the port the app runs on (Spring Boot default is 8080)
EXPOSE 5001

# Set environment variables from local.env (if needed inside Docker)
# ENV $(cat local.env | xargs)

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]